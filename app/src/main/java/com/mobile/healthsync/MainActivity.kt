package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.patientProfile.PatientProfile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, PatientProfile::class.java)
        startActivity(intent)
    }
}
