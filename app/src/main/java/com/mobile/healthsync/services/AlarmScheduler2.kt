package com.mobile.healthsync.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class AlarmScheduler2(private val context: Context) {

    fun scheduleAlarm() {
        val ALARM_REQUEST_CODE = 123
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyAlarmReceiver2::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm to trigger at approximately the same time every day (e.g., 9 PM and 30 minutes)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10) // Set hour to 21 (9 PM)
            set(Calendar.MINUTE, 30)      // Set minute to 30
            set(Calendar.SECOND, 0)
            // Optionally, you can set milliseconds to 0 as well
            set(Calendar.MILLISECOND, 0)
        }

        // Check if the alarm time has already passed today
        val currentTime = Calendar.getInstance()
        if (calendar.timeInMillis <= currentTime.timeInMillis) {
            // If the alarm time has already passed today, set it for tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Log the scheduled alarm time for debugging
        Log.d("AlarmScheduler", "Scheduled Alarm Time: ${calendar.time}")

        // Schedule the alarm using AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d("AlarmScheduler", "Alarm scheduled for daily execution")
    }
}
