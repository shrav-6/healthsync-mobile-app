package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.login.LoginActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val intent = Intent(
//                this@MainActivity,
//                DoctorProfile::class.java
//            )
//        startActivity(intent)

        val from = intent.getStringExtra("from")
        if(from == "patient to do") {
            val intent = Intent(this, PatientDashboard::class.java)
            startActivity(intent)
        }
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(
                this@MainActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }, 3000)
    }
}