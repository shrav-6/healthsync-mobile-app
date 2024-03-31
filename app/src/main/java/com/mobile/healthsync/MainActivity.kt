package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.events.EventsActivity
import com.mobile.healthsync.views.login.LoginActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.patientDashboard.PatientInsights
import com.mobile.healthsync.views.patientDashboard.PatientToDo
import com.mobile.healthsync.views.signUp.SignupActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val from = intent.getStringExtra("from")
        if(from == "patient to do") {
            val intent = Intent(this, PatientDashboard::class.java)
            startActivity(intent)
        }
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(
                this@MainActivity,
                PatientDashboard::class.java
            )
            startActivity(intent)
        }, 3000)
    }
}