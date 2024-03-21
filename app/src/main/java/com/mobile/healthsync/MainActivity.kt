package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.integerArrayResource
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.views.patientDashboard.DoctorInfoActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, DoctorInfoActivity::class.java)
        intent.putExtra("patient_id",784)
        intent.putExtra("doctor_id",663)
        startActivity(intent)
    }
}
