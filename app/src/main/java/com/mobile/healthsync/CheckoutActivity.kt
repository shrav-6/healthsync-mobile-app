package com.mobile.healthsync

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.healthsync.PaymentAPI.ApiUtilities
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.mobile.healthsync.PaymentUtils.PUBLISH_KEY
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore


class CheckoutActivity : AppCompatActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerID: String
    private lateinit var ephemeralKey: String
    private lateinit var clientSecret: String

    private lateinit var tvDoctorName: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private var amount: Double = 0.0
    private var totalAmount: Double = 0.0
    private var rewardPoints: Long = 0
    private var patientId: Int = 251 // Placeholder. Replace with actual ID passed via Intent
    private var appointmentId: Int = 663 // Placeholder
    private var doctorId: Int = 663 // Placeholder
    private var app_date: String = "Date" // Placeholder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Retrieve passed intent data
//        patientId = intent.getStringExtra("PATIENT_ID") ?: "default_patient_id"
//        appointmentId = intent.getStringExtra("APPOINTMENT_ID") ?: "default_appointment_id"
//        doctorId = intent.getStringExtra("DOCTOR_ID") ?: "default_doctor_id"
//        patientId = intent.getIntExtra("PATIENT_ID", -1)
//        appointmentId = intent.getIntExtra("APPOINTMENT_ID", -1)
//        doctorId = intent.getIntExtra("DOCTOR_ID", -1)

        PaymentConfiguration.init(this, PUBLISH_KEY)
        getCustomerID()
        setupViews()
        fetchAppointmentAndPatientDetails(appointmentId, doctorId, patientId)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    private fun setupViews() {
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvDateTime = findViewById(R.id.tvDateTime)
        tvAmount = findViewById(R.id.tvAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        val btnRedeemPoints = findViewById<Button>(R.id.btnRedeemPoints)
        val buttonPay = findViewById<Button>(R.id.buttonPay)

        btnRedeemPoints.setOnClickListener {
            redeemPoints()
        }

        buttonPay.setOnClickListener {
            if (!this::clientSecret.isInitialized) {
                Toast.makeText(this, "Client secret not initialized", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            paymentFlow()
        }
    }

    private fun fetchAppointmentAndPatientDetails(appointmentId: Int, doctorId: Int, patientId: Int) {
        val db = FirebaseFirestore.getInstance()

        // Fetch patient reward points
        db.collection("patients")
            .whereEqualTo("patient_id", patientId)
            .get()
            .addOnSuccessListener { patients ->
                for (patientDoc in patients) {
                    rewardPoints = patientDoc.getLong("reward_points") ?: 0
                    // Optionally update the UI or a variable to reflect the patient's reward points
                }
            }.addOnFailureListener { e ->
                Log.e("CheckoutActivity", "Error fetching patient reward points", e)
            }

        // Fetch appointment details
        db.collection("appointments")
            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { appointments ->
                for (appointmentDoc in appointments) {
                    val appointmentDate = appointmentDoc.getString("date")
                    tvDateTime.text = "Date & Time: $appointmentDate"
                }
            }.addOnFailureListener { e ->
                Log.e("CheckoutActivity", "Error fetching appointment details", e)
            }

        // Fetch doctor details
        db.collection("doctors")
            .whereEqualTo("doctor_id", doctorId)
            .get()
            .addOnSuccessListener { doctors ->
                for (doctorDoc in doctors) {
                    // Assuming 'doctor_info' is a field containing a map of doctor information
                    val doctorInfo = doctorDoc.get("doctor_info") as Map<String, Any>?
                    val doctorName = doctorInfo?.get("name").toString()
                    val consultationFee = (doctorInfo?.get("consultation_fees") as Number?)?.toDouble() ?: 0.0

                    tvDoctorName.text = "Doctor: $doctorName"
                    tvAmount.text = "Amount: $$consultationFee"
                    amount = consultationFee
                    totalAmount = consultationFee
                    tvTotalAmount.text = "Total Amount: $$totalAmount"
                }
            }.addOnFailureListener { e ->
                Log.e("CheckoutActivity", "Error fetching doctor details", e)
            }
    }




    private fun redeemPoints() {
        // Placeholder function. Implement points redemption logic.
    }



    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "HealthSync",
                PaymentSheet.CustomerConfiguration(
                    customerID,
                    ephemeralKey
                )
            )
        )
    }

    private var apiInterface = ApiUtilities.getApiInterface()

    private fun getCustomerID() {
        val db = FirebaseFirestore.getInstance()
        // Query for the patient document by the patient_id field
        db.collection("patients")
            .whereEqualTo("patient_id", patientId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Assuming patient_id is unique, there should only be one matching document
                    val document = documents.documents[0]
                    val stripeCustomerId = document.getString("stripe_customer_id")
                    if (!stripeCustomerId.isNullOrEmpty()) {
                        customerID = stripeCustomerId
                        getEphemeralKey(customerID)
                    } else {
                        createStripeCustomer(document.id)
                    }
                } else {
                    Log.e("CheckoutActivity", "No matching patient document found.")
                }
            }.addOnFailureListener { exception ->
                Log.e("CheckoutActivity", "Error fetching patient document", exception)
            }
    }


    private fun createStripeCustomer(docId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val res = apiInterface.getCustomer()
            withContext(Dispatchers.Main) {
                if (res.isSuccessful && res.body() != null) {
                    customerID = res.body()!!.id
                    // Now, save the new customer ID in Firestore under the specific patient's document
                    val db = FirebaseFirestore.getInstance()
                    db.collection("patients").document(docId)
                        .update("stripe_customer_id", customerID)
                        .addOnSuccessListener {
                            Log.d("CheckoutActivity", "DocumentSnapshot successfully updated with new customer ID.")
                            getEphemeralKey(customerID)
                        }
                        .addOnFailureListener { e ->
                            Log.w("CheckoutActivity", "Error updating document", e)
                        }
                } else {
                    Log.e("CheckoutActivity", "Failed to create new Stripe customer")
                }
            }
        }
    }


    private fun getEphemeralKey(customerID: String) {
        lifecycleScope.launch(Dispatchers.IO){

            val res= apiInterface.getEphemeralKey(customerID)
            withContext(Dispatchers.Main){

                if(res.isSuccessful && res.body()!=null){
                    ephemeralKey= res.body()!!.id
                    println("EPHEMERAL KEY: $ephemeralKey")
                    getPaymentIntent(customerID, ephemeralKey, (totalAmount*100).toString())
                }
            }
        }
    }

    private fun getPaymentIntent(customerID: String, ephemeralKey: String, totalAmount: String) {
        lifecycleScope.launch(Dispatchers.IO){

            val res= apiInterface.getPaymentIntent(customerID, totalAmount)
            withContext(Dispatchers.Main){
                println("INSIDE DISPATCHER")
                println(res.raw())
                if(res.isSuccessful && res.body()!=null){
                    println("RES SUCCESS")
                    clientSecret= res.body()!!.client_secret
                    println("CLIENT SECRET: $clientSecret")

                    Toast.makeText(this@CheckoutActivity, "Proceed to Payment", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
        if(paymentSheetResult is PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment DONE", Toast.LENGTH_SHORT).show()
        }
        if(paymentSheetResult is PaymentSheetResult.Canceled){
            Toast.makeText(this, "Payment CANCELED", Toast.LENGTH_SHORT).show()
        }
    }

}
