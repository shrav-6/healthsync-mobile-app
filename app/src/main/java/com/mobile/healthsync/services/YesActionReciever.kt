package com.mobile.healthsync.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class YesActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", -1)
        val prescriptionId = intent.getIntExtra("prescriptionId", -1)
        if (prescriptionId != -1) {
            // Handle "Yes" action here
            Log.d(TAG, "YesActionReceiver: Yes button clicked for prescription ID $prescriptionId")

            // Dismiss the notification
            dismissNotification(context, notificationId)
        }
    }

    private fun dismissNotification(context: Context, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }

    companion object {
        private const val TAG = "YesActionReceiver"
    }
}
