package com.mobile.healthsync.model;

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

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



