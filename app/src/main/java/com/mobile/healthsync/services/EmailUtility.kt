package com.mobile.healthsync.services

import android.content.Context
import android.os.Handler
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmailUtility(private val context: Context) {

    interface EmailCallback {
        fun onSuccess()
        fun onError(error: String)
    }

    suspend fun sendConfirmationEmail(
        receiverEmail: String = "merinmarysaju11@gmail.com",
        doctorName: String = "Dr.Sruthi Shaji",
        startTime: String = "8.29 PM",
        timeStamp: String = "5/25/2023",
        subject: String = "Booking Confirmation",
        callback: EmailCallback
    ) {
        try {
            val senderEmail = "health.sync19@gmail.com"
            val senderPassword = "kcoxhvbqiptjedcx"//to be edited by mohammed

            val properties = Properties()
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = "587"
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.starttls.enable"] = "true"

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(senderEmail))
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))
            mimeMessage.subject = subject
            val emailText =
                "Your booking with $doctorName on $timeStamp at $startTime is confirmed. Thank you for choosing our service!"

            mimeMessage.setText(emailText)

            suspendCoroutine<Unit> { continuation ->
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val transport = session.getTransport("smtp")
                        transport.connect(senderEmail, senderPassword)
                        transport.sendMessage(mimeMessage, mimeMessage.allRecipients)
                        transport.close()

                        continuation.resume(Unit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.onError("Error sending confirmation email: $e")
                    }
                }
            }

            callback.onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError("Error sending confirmation email: $e")
        }
    }

    fun showSuccessToast() {
        Toast.makeText(
            context,
            "Booking confirmation email sent successfully!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showErrorToast(error: String) {
        Handler(context.mainLooper).post {
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
/*val emailUtility = EmailUtility(this)

       // Launch a coroutine using GlobalScope
       GlobalScope.launch(Dispatchers.Main) {
           try {
               emailUtility.sendConfirmationEmail(callback = object : EmailUtility.EmailCallback {
                   override fun onSuccess() {
                       emailUtility.showSuccessToast()
                   }

                   override fun onError(error: String) {
                       emailUtility.showErrorToast(error)
                   }
               })
           } catch (e: Exception) {
               e.printStackTrace()
           }
       }*/
