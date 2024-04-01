package com.mobile.healthsync.model;

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

/**
 * Data class representing an event.
 *
 * @param contactInfo The contact information related to the event.
 * @param dateAndTime The date and time of the event.
 * @param datePublished The date when the event was published.
 * @param description The description of the event.
 * @param eventID The unique identifier of the event.
 * @param eventName The name of the event.
 * @param locationAndVenue The location and venue of the event.
 * @param meetingLink The meeting link associated with the event.
 * @param organizer The organizer of the event.
 * @param status The status of the event.
 * @param type The type of the event.
 */
data class Event(
    @get:PropertyName("contactInfo")
    @set:PropertyName("contactInfo")
    var contactInfo: String = "",

    @get:PropertyName("dateAndTime")
    @set:PropertyName("dateAndTime")
    var dateAndTime: String = "",

    @get:PropertyName("datePublished")
    @set:PropertyName("datePublished")
    var datePublished: String = "",

    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("eventID")
    @set:PropertyName("eventID")
    var eventID: Int = 0,

    @get:PropertyName("eventName")
    @set:PropertyName("eventName")
    var eventName: String = "",

    @get:PropertyName("locationAndVenue")
    @set:PropertyName("locationAndVenue")
    var locationAndVenue: String = "",

    @get:PropertyName("meetingLink")
    @set:PropertyName("meetingLink")
    var meetingLink: String = "",

    @get:PropertyName("organizer")
    @set:PropertyName("organizer")
    var organizer: String = "",

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "",

    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String = ""
) : Serializable



