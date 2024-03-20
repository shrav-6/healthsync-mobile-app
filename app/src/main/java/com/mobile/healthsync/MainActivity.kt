package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start CheckoutActivity for testing
        val checkoutIntent = Intent(this, CheckoutActivity::class.java)
        startActivity(checkoutIntent)

        // val intent = Intent(this, PatientDashboard::class.java)
//        startActivity(intent)
    }
}



