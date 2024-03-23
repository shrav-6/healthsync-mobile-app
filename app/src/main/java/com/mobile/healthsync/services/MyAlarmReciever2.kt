package com.mobile.healthsync.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MyAlarmReceiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MyBackgroundService2::class.java)
        context.startService(serviceIntent)
    }
}
