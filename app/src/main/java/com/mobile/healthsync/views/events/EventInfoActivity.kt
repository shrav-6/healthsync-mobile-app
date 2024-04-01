package com.mobile.healthsync.views.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Event

/**
 * Activity for displaying detailed information about an event.
 */
class EventInfoActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var icon: ImageView
    private lateinit var eventTitle: TextView
    private lateinit var eventOrganizer: TextView
    private lateinit var datePublished: TextView
    private lateinit var scheduledTime: TextView
    private lateinit var locationAndVenue: TextView
    private lateinit var meetingLink: TextView
    private lateinit var eventDescription: TextView
    private lateinit var rsvpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val event = intent.getSerializableExtra("currentEvent") as? Event

        backButton = findViewById(R.id.back)
        icon = findViewById(R.id.icon)
        eventTitle = findViewById(R.id.eventTitle)
        eventOrganizer = findViewById(R.id.eventOrganizer)
        datePublished = findViewById(R.id.datePublished)
        scheduledTime = findViewById(R.id.scheduledTime)
        locationAndVenue = findViewById(R.id.locationAndVenue)
        meetingLink = findViewById(R.id.meetingLink)
        eventDescription = findViewById(R.id.eventDescription)
        rsvpButton = findViewById(R.id.rsvpButton)

        if (event != null) {
            eventTitle.text = event.eventName
            eventOrganizer.text = event.organizer
            datePublished.text = "Date Published: " + event.datePublished
            scheduledTime.text = "Scheduled Date and Time: " + event.dateAndTime

            if (event.locationAndVenue != "") {
                locationAndVenue.text = "Venue: " + event.locationAndVenue
            } else {
                locationAndVenue.text = "Venue: -"
            }

            if (event.meetingLink != "") {
                meetingLink.text = event.meetingLink
            } else {
                meetingLink.text = "No Meeting Link"
            }

            eventDescription.text = event.description

            when (event.type) {
                "donation" -> icon.setImageResource(R.drawable.donation)
                "fundraiser" -> icon.setImageResource(R.drawable.fund)
                "info session" -> icon.setImageResource(R.drawable.info)
                "volunteer" -> icon.setImageResource(R.drawable.volunteer)
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        rsvpButton.setOnClickListener {
            initiateEmail(
                eventTitle.text.toString(),
                eventOrganizer.text.toString(),
                scheduledTime.text.toString()
            )
        }
    }

    /**
     * Initiate email with event details for RSVP.
     *
     * R. Rishabh007 Follow, “How to send an email from an android application?,” GeeksforGeeks, 17-Jan-2020. [Online]. Available: https://www.geeksforgeeks.org/how-to-send-an-email-from-your-android-app/. [Accessed: 31-Mar-2024].
     */
    private fun initiateEmail(eventName: String, eventOrganizer: String, scheduledTime: String) {
        val emailTo = "health.sync19@gmail.com"
        val emailSubject = "RSVP for Event: $eventName"
        val emailBody =
            "Hi HealthSync,\n" + "\n" +
                    "I'm thrilled to RSVP for the $eventName, organized by $eventOrganizer on $scheduledTime.\n" +
                    "\n" +
                    "Looking forward to it!\n"

        // Intent object with action attribute as ACTION_SEND
        val intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)

        // MIME type indicates that the content of the Intent is an email message
        intent.type = "message/rfc822"

        // startActivity with intent with Email client using createChooser function
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }
}