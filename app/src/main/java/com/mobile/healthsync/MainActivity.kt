package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.healthsync.PaymentAPI.ApiUtilities
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet

import com.mobile.healthsync.PaymentUtils.PUBLISH_KEY

import com.stripe.android.paymentsheet.PaymentSheetResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerID: String
    lateinit var ephemeralKey: String
    lateinit var clientSecret: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // addRewardPointsToPatients()

        PaymentConfiguration.init(this, PUBLISH_KEY)

        getCustomerID()

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            paymentFlow()
        }

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

//        val intent = Intent(this, PatientDashboard::class.java)
//        startActivity(intent)
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
        lifecycleScope.launch(Dispatchers.IO){

            val res= apiInterface.getCustomer()
            withContext(Dispatchers.Main){

                if(res.isSuccessful && res.body()!=null){
                    customerID= res.body()!!.id
                    println("CUSTOMER ID: $customerID")
                    getEphemeralKey(customerID)
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
                    getPaymentIntent(customerID, ephemeralKey)
                }
            }
        }
    }

    private fun getPaymentIntent(customerID: String, ephemeralKey: String) {
        lifecycleScope.launch(Dispatchers.IO){

            val res= apiInterface.getPaymentIntent(customerID, "4500")
            withContext(Dispatchers.Main){
                println("INSIDE DISPATCHER")
                println(res.raw())
                if(res.isSuccessful && res.body()!=null){
                    println("RES SUCCESS")
                    clientSecret= res.body()!!.client_secret
                    println("CLIENT SECRET: $clientSecret")

                    Toast.makeText(this@MainActivity, "Proceed to Payment", Toast.LENGTH_SHORT).show()
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
