package com.mobile.healthsync.videoCalling

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import kotlin.random.Random
import android.widget.Toast

class VideoCallActivity : AppCompatActivity() {

    private val meetLinks = listOf(
        "https://meet.google.com/opk-pbqh-pqv",
        "https://meet.google.com/unq-ktip-voj",
        "https://meet.google.com/vpe-cinr-pgf",
        "https://meet.google.com/koz-wooa-dvf",
        "https://meet.google.com/die-dpuo-ios",
        "https://meet.google.com/eqv-ture-bfv",
        "https://meet.google.com/fjr-ugxs-oii",
        "https://meet.google.com/bif-hpvc-ezc"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        val meetButton = findViewById<Button>(R.id.meetButton)
        val selectedUrlTextView = findViewById<TextView>(R.id.selectedUrlTextView)

        meetButton.setOnClickListener {
            val meetLink = generateRandomMeetLink()
            openMeetLinkInBrowser(meetLink)
            saveAppointment(meetLink)
            setClickableLink(meetLink, selectedUrlTextView)
        }
    }

    override fun onPause() {
        super.onPause()
        // App goes to the background
        // You can perform any actions here when the app goes to the background
    }

    override fun onResume() {
        super.onResume()
        // App comes back to the foreground
        // Show toast message saying "Appointment done"
        Toast.makeText(this, "Appointment done", Toast.LENGTH_SHORT).show()
    }

    private fun generateRandomMeetLink(): String {
        val randomIndex = Random.nextInt(meetLinks.size)
        return meetLinks[randomIndex]
    }

    private fun openMeetLinkInBrowser(meetLink: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetLink))
        startActivity(browserIntent)
    }

    private fun saveAppointment(meetLink: String) {
        println("Appointment saved: $meetLink")
    }

    private fun setClickableLink(link: String, textView: TextView) {
        val text = "Appointment Link: $link"
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openMeetLinkInBrowser(link)
            }
        }
        spannableString.setSpan(clickableSpan, 16, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }
}

