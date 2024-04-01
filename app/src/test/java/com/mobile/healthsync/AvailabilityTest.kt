package com.mobile.healthsync

import com.mobile.healthsync.model.Availability
import com.mobile.healthsync.model.Slot
import org.junit.Assert.assertEquals
import org.junit.Test

class AvailabilityTest {

    @Test
    fun testPropertyInitialization() {
        val availability = Availability(true,
            listOf(
                Slot(1,"7:00 AM","9:00 AM"),
                Slot(3, "8:00 AM","10:00 AM")
            )
        )
        assertEquals(true, availability.is_available)
        assertEquals(2, availability.slots?.size)
    }
}
