package com.mobile.healthsync.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {

    @Test
    fun testEventProperties() {
        // Create an Event instance
        val event = Event(
            contactInfo = "John Doe",
            dateAndTime = "2022-05-01T12:00:00",
            datePublished = "2022-04-01",
            description = "Event Description",
            eventID = 1,
            eventName = "Event Name",
            locationAndVenue = "Venue Name, City",
            meetingLink = "https://example.com/meeting",
            organizer = "Organizer Name",
            status = "Active",
            type = "Workshop"
        )

        // Assert Event properties
        assertEquals("John Doe", event.contactInfo)
        assertEquals("2022-05-01T12:00:00", event.dateAndTime)
        assertEquals("2022-04-01", event.datePublished)
        assertEquals("Event Description", event.description)
        assertEquals(1, event.eventID)
        assertEquals("Event Name", event.eventName)
        assertEquals("Venue Name, City", event.locationAndVenue)
        assertEquals("https://example.com/meeting", event.meetingLink)
        assertEquals("Organizer Name", event.organizer)
        assertEquals("Active", event.status)
        assertEquals("Workshop", event.type)
    }

}