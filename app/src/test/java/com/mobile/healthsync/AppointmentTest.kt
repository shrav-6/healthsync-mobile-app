package com.mobile.healthsync

import com.mobile.healthsync.model.Appointment
import org.junit.Assert.assertEquals
import org.junit.Test

class AppointmentTest {

    @Test
    fun testPropertyInitialization() {
        val appointment = Appointment(1, 2, 3, "2024-04-01", 4, "10:00 AM")
        assertEquals(1, appointment.appointment_id)
        assertEquals(2, appointment.doctor_id)
        assertEquals(3, appointment.patient_id)
        assertEquals("2024-04-01", appointment.date)
        assertEquals(4, appointment.slot_id)
        assertEquals("10:00 AM", appointment.start_time)
        assertEquals(-1, appointment.payment_id)
        assertEquals(false, appointment.appointment_status)
    }
}
