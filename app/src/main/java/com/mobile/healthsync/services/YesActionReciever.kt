package com.mobile.healthsync.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.mobile.healthsync.repository.PrescriptionRepository

class YesActionReceiver : BroadcastReceiver() {

    private  lateinit var prescriptionRepository: PrescriptionRepository

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", -1)
//<<<<<<< HEAD
        val prescriptionId1: String = "122";
        if (notificationId != -1) {
            // Handle "Yes" action here
            Log.d(TAG, "YesActionReceiver: Yes button clicked for notification ID $notificationId")
            prescriptionRepository.updatePatientMedicineIntake(prescriptionId1)
//=======
            val prescriptionId = intent.getIntExtra("prescriptionId", -1)
            if (prescriptionId != -1) {
                // Handle "Yes" action here
                Log.d(
                    TAG,
                    "YesActionReceiver: Yes button clicked for prescription ID $prescriptionId"
                )
//>>>>>>> 11-email-confirmation-for-booking

                // Dismiss the notification
                dismissNotification(context, notificationId)
            }
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
