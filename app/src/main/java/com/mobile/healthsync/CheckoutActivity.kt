package com.mobile.healthsync

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.PaymentAPI.ApiUtilities
import com.mobile.healthsync.PaymentUtils.PUBLISH_KEY
import com.mobile.healthsync.services.EmailUtility
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.Random




class CheckoutActivity : BaseActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerID: String
    private lateinit var ephemeralKey: String
    private lateinit var clientSecret: String

    private lateinit var tvDoctorName: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvTotalPoints: TextView
    private lateinit var tvTotalAmount: TextView //tvConsultationFee
    private lateinit var tvConsultationFee: TextView
    private var amount: Double = 0.0
    private var totalAmount: Double = 0.0
    private var rewardPoints: Long = 0
    private var patientId: Int = 251 // Placeholder. Replace with actual ID passed via Intent
    private var appointmentId: Int = 663 // Placeholder
    private var doctorId: Int = 663 // Placeholder
    private var app_date: String = "Date" // Placeholder
    private var discountAmount: Double = 0.0
    private var appointmentDate:String =""
    private var appointmentStart:String=""
    private var patientEmailAddress:String=""
    private var doctorName:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)


//        patientId = intent.getStringExtra("PATIENT_ID") ?: "default_patient_id"
//        appointmentId = intent.getStringExtra("APPOINTMENT_ID") ?: "default_appointment_id"
//        doctorId = intent.getStringExtra("DOCTOR_ID") ?: "default_doctor_id"

        patientId = getSharedPreferences("preferences", Context.MODE_PRIVATE)
            .getString("patient_id", "-1")?.toInt() ?: -1
        appointmentId = intent.getIntExtra("appointment_id", -1)
        doctorId = intent.getIntExtra("doctor_id", -1)

        PaymentConfiguration.init(this, PUBLISH_KEY)


        fetchAppointmentAndPatientDetails(appointmentId, doctorId, patientId)
        getCustomerID()
        setupViews()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    private fun setupViews() {
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvDateTime = findViewById(R.id.tvDateTime)
        tvTotalPoints = findViewById(R.id.tvTotalPoints)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvConsultationFee = findViewById(R.id.tvConsultationFee)
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
                    patientEmailAddress=patientDoc.getString("email")?:""
                    tvTotalPoints.text = "Total Points: $rewardPoints"
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
                    appointmentDate = appointmentDoc.getString("date").toString()
                    appointmentStart = appointmentDoc.getString("start_time").toString()
                    tvDateTime.text = "Date & Time: $appointmentDate $appointmentStart"
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
                    doctorName = doctorInfo?.get("name").toString()
                    val consultationFee = (doctorInfo?.get("consultation_fees") as Number?)?.toDouble() ?: 0.0

                    tvDoctorName.text = "Doctor: $doctorName"

                    amount = consultationFee
                    totalAmount = consultationFee
                    tvConsultationFee.text = "Consultation Fee: $$amount"
                    tvTotalAmount.text = "Total Amount: $$totalAmount"
                }
            }.addOnFailureListener { e ->
                Log.e("CheckoutActivity", "Error fetching doctor details", e)
            }
    }




    private fun redeemPoints() {
        val pointsNeededForDiscount = 100
        val discountPer100Points = 5.0
        val redeemablePoints = rewardPoints - (rewardPoints % pointsNeededForDiscount) // Points to the nearest 100
        val discountAmount = (redeemablePoints / pointsNeededForDiscount) * discountPer100Points

        if (redeemablePoints >= pointsNeededForDiscount) {
            totalAmount -= discountAmount
            rewardPoints -= redeemablePoints // Deduct only redeemable points

            // Update UI
            tvTotalAmount.text = "Total Amount: $${String.format("%.2f", totalAmount)}"
            tvTotalPoints.text = "Points Used: $redeemablePoints - New Balance: $rewardPoints"

            // Update Firestore with new points balance
            updatePatientRewardPoints(rewardPoints)
            getPaymentIntent(customerID, ephemeralKey, formatDoubleToStringWithoutDecimal(totalAmount*100))

            Toast.makeText(this, "Discount Applied: $$discountAmount", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Insufficient points for discount. 100 points needed.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updatePatientRewardPoints(newRewardPoints: Long) {
        val db = FirebaseFirestore.getInstance()
        // Assuming patient_id uniquely identifies the document
        db.collection("patients")
            .whereEqualTo("patient_id", patientId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    db.collection("patients").document(documentSnapshot.id)
                        .update("reward_points", newRewardPoints)
                        .addOnSuccessListener {
                            Log.d("CheckoutActivity", "Patient reward points updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("CheckoutActivity", "Error updating patient reward points.", e)
                        }
                }
            }
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
                    val tamount=formatDoubleToStringWithoutDecimal(totalAmount*100)
                    getPaymentIntent(customerID, ephemeralKey, tamount)
                }
            }
        }
    }

    private fun getPaymentIntent(customerID: String, ephemeralKey: String, totalAmountToPay: String) {
        lifecycleScope.launch(Dispatchers.IO){

            val res= apiInterface.getPaymentIntent(customerID, totalAmountToPay)
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

    fun formatDoubleToStringWithoutDecimal(value: Double): String {
        val formatter = DecimalFormat("0.#") // Define a pattern to show at least one digit before the decimal point
        return formatter.format(value)
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
        if(paymentSheetResult is PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment DONE", Toast.LENGTH_SHORT).show()
            updateAppointmentStatus(appointmentId)
            createPaymentRecordAndLinkToAppointment(appointmentId, patientId, totalAmount)
            // Optionally show a success dialog or navigate to another screen
            showSuccessMessage()
        }
        if(paymentSheetResult is PaymentSheetResult.Canceled){
            Toast.makeText(this, "Payment CANCELED", Toast.LENGTH_SHORT).show()
        }
        if(paymentSheetResult is PaymentSheetResult.Failed){
            Toast.makeText(this, "Payment Failed: ${paymentSheetResult.error}", Toast.LENGTH_SHORT).show()
            val returnIntent = Intent()
            returnIntent.putExtra("status", "Operation failure")
            setResult(0, returnIntent)
            finish()
        }
    }

    private fun updateAppointmentStatus(appointmentId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments")
            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("appointments").document(document.id)
                        .update("appointment_status", true)
                        .addOnSuccessListener {
                            Log.d("CheckoutActivity", "Appointment status updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("CheckoutActivity", "Error updating appointment status.", e)
                        }
                }
            }
    }

    private fun createPaymentRecordAndLinkToAppointment(appointmentId: Int, patientId: Int, amountPaid: Double) {
        val db = FirebaseFirestore.getInstance()
        val uniquePaymentId = generateUniquePaymentId() // Generate a unique payment ID
        val paymentData = hashMapOf(
            "amount_paid" to "$${String.format("%.2f", amountPaid)}",
            "email_notification" to false,
            "payment_id" to uniquePaymentId,
            "payment_status" to true,
            "timestamp" to System.currentTimeMillis().toString()
        )

        db.collection("payments").add(paymentData)
            .addOnSuccessListener { documentReference ->
                Log.d("CheckoutActivity", "Payment record created successfully with ID: $uniquePaymentId")
                linkPaymentIdToAppointment(uniquePaymentId, appointmentId) // Use the generated unique payment ID
                addRewardPoints(patientId, 20)  // Assuming 20 points are added for each successful payment.
            }
            .addOnFailureListener { e ->
                Log.e("CheckoutActivity", "Error creating payment record.", e)
            }
    }


    private fun linkPaymentIdToAppointment(paymentId: Int, appointmentId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments")
            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("appointments").document(document.id)
                        .update("payment_id", paymentId)
                        .addOnSuccessListener {
                            Log.d("CheckoutActivity", "Linked payment ID $paymentId to appointment successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("CheckoutActivity", "Error linking payment ID $paymentId.", e)
                        }
                }
            }
    }


    private fun addRewardPoints(patientId: Int, pointsToAdd: Long) {
        val db = FirebaseFirestore.getInstance()
        db.collection("patients")
            .whereEqualTo("patient_id", patientId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val currentPoints = document.getLong("reward_points") ?: 0
                    val newPoints = currentPoints + pointsToAdd
                    db.collection("patients").document(document.id)
                        .update("reward_points", newPoints)
                        .addOnSuccessListener {
                            Log.d("CheckoutActivity", "Reward points added successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("CheckoutActivity", "Error adding reward points.", e)
                        }
                }
            }
    }

    private fun generateUniquePaymentId(): Int {
        val timestampPart = (System.currentTimeMillis() % 100000).toInt() // Last 5 digits of the current timestamp
        val randomPart = Random().nextInt(900) + 100 // Ensures a 3-digit random number
        return timestampPart * 1000 + randomPart // Combines both parts
    }

    private fun showSuccessMessage() {
        AlertDialog.Builder(this)
            .setTitle("Payment Successful")
            .setMessage("Your appointment has been booked successfully, and you earned 20 reward points!")
            .setPositiveButton("OK") { dialog, which ->
                val returnIntent = Intent()
                returnIntent.putExtra("status", "Operation Success")
                returnIntent.putExtra("payment_id", 1)
                val emailUtility = EmailUtility(this)

                // Launch a coroutine using GlobalScope
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        emailUtility.sendConfirmationEmail(receiverEmail = patientEmailAddress, doctorName =doctorName, startTime = appointmentStart , timeStamp = appointmentDate, callback = object : EmailUtility.EmailCallback {
                            override fun onSuccess() {
                                emailUtility.showSuccessToast()
                            }

                            override fun onError(error: String) {
                                emailUtility.showErrorToast(error)
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                setResult(1, returnIntent)
                finish()
            }
            .setCancelable(false) // Prevent dialog dismissal on back press
            .show()
    }





}
