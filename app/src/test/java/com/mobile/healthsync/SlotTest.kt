package com.mobile.healthsync

import com.mobile.healthsync.model.Slot
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class SlotTest {

    @Test
    fun testSlotInitialization() {
        val slot = Slot(1, "9:00 AM", "10:00 AM")
        assertEquals(1, slot.slot_id)
        assertEquals("9:00 AM", slot.start_time)
        assertEquals("10:00 AM", slot.end_time)
        assertFalse(slot.isBooked())
    }

    @Test
    fun testSetAsBooked() {
        val slot = Slot()
        slot.setAsBooked()
        assertTrue(slot.isBooked())
    }

    @Test
    fun testRemoveBooking() {
        val slot = Slot()
        slot.setAsBooked()
        slot.removeBooking()
        assertFalse(slot.isBooked())
    }

    @Test
    fun testIsBooked() {
        val slot = Slot()
        assertFalse(slot.isBooked())

        slot.setAsBooked()
        assertTrue(slot.isBooked())

        slot.removeBooking()
        assertFalse(slot.isBooked())
    }
}
