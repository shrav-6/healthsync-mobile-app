package com.mobile.healthsync.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mobile.healthsync.MainActivity
import com.mobile.healthsync.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            // Handle notification payload
            val title = it.title ?: "Notification Title"
            val body = it.body ?: "Notification Body"

            // Log notification data for debugging
            Log.d(TAG, "Notification Title: $title, Body: $body")

            // Check if the message contains a data payload (optional)
            remoteMessage.data.isNotEmpty().let {
                Log.d(TAG, "Data Payload: ${remoteMessage.data}")

                // Handle data payload
                // You can extract data from the message and trigger specific actions in your app
                if (remoteMessage.data.containsKey("sendReminder")) {
                    // Handle send-reminder data
                    val reminderData = remoteMessage.data["sendReminder"]
                    Log.d(TAG, "Send Reminder Data: $reminderData")
                    val prescriptionId = remoteMessage.data["prescriptionId"]?.toInt()

                    // Show reminder notification with action buttons
                    if (prescriptionId != null) {
                        showReminderNotification(title, body,prescriptionId)
                    }
                } else {
                    // Show basic notification
                    sendBasicNotification(title, body)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // If the FCM token is refreshed, update it in your backend or preferences
        Log.d(TAG, "Refreshed Token: $token")
        // You can send the new token to your server or save it locally
        // Example: SaveTokenLocally(token)
    }

    private fun sendBasicNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.basic_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Basic Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(BASIC_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun showReminderNotification(title: String, messageBody: String,prescriptionId:Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val yesIntent = Intent(this, YesActionReceiver::class.java)
        yesIntent.putExtra("notificationId", REMINDER_NOTIFICATION_ID)
        yesIntent.putExtra("prescriptionId", prescriptionId)
        val noIntent = Intent(this, NoActionReceiver::class.java)
        noIntent.putExtra("notificationId", REMINDER_NOTIFICATION_ID)
        noIntent.putExtra("prescriptionId", prescriptionId)

        val yesPendingIntent = PendingIntent.getBroadcast(this, 0, yesIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val noPendingIntent = PendingIntent.getBroadcast(this, 0, noIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.reminder_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .addAction(R.drawable.ic_launcher_background, "Yes", yesPendingIntent)
            .addAction(R.drawable.ic_launcher_background, "No", noPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val BASIC_NOTIFICATION_ID = 1
        private const val REMINDER_NOTIFICATION_ID = 2
    }
}
