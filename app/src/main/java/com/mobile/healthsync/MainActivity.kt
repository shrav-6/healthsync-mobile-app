package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.mobile.healthsync.services.AlarmScheduler
import com.mobile.healthsync.services.AlarmScheduler2
import com.mobile.healthsync.views.signUp.SignupActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Get the Firebase token
        // Get the Firebase token
//        FirebaseMessaging.getInstance().getToken()
//            .addOnCompleteListener { task: Task<String> ->
//                if (task.isSuccessful) {
//                    val token = task.result
//                    Log.d("firebase token", "Firebase Token: $token")
//                    // Handle the token (e.g., send it to your server)
//                } else {
//                    Log.e("firebase token", "Failed to get Firebase token: " + task.exception)
//                }
//            }

        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(
                this@MainActivity,
                SignupActivity::class.java
            )
            startActivity(intent)
        }, 3000)
        // Create an instance of AlarmScheduler and schedule the alarm
        val alarmScheduler = AlarmScheduler(this)
        alarmScheduler.scheduleAlarm()
        val alarmScheduler2 = AlarmScheduler2(this)
        alarmScheduler2.scheduleAlarm()

    }
}