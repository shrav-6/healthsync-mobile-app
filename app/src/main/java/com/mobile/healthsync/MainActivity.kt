package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import com.mobile.healthsync.views.events.EventsActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val handler = Handler()
//        handler.postDelayed({
//            val intent = Intent(
//                this@MainActivity,
//                SignupActivity::class.java
//            )
//            startActivity(intent)
//        }, 3000)
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(
                this@MainActivity,
                EventsActivity::class.java
            )
            startActivity(intent)
        }, 3000)
    }
}
