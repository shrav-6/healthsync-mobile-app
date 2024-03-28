package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.doctorProfile.DoctorProfile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        val intent = Intent(this, DoctorProfile::class.java)
        startActivity(intent)

//        val handler = Handler()
//        handler.postDelayed({
//            val intent = Intent(
//                this@MainActivity,
//                SignupActivity::class.java
//            )
//            startActivity(intent)
//        }, 3000)

    }
}