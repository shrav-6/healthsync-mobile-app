package com.mobile.healthsync.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PrescriptionTest {

    @Test
    fun testPrescriptionProperties() {
        // Create a Prescription instance
        val prescription = Prescription(
            appointmentId = 1,
            prescriptionId = 1001,
            medicines = hashMapOf(
                "Medicine1" to Prescription.Medicine(
                    name = "Medicine1",
                    dosage = "10mg",
                    numberOfDays = 7,
                    schedule = Prescription.Medicine.DaySchedule(
                        morning = Prescription.Medicine.DaySchedule.Schedule(doctorSaid = true, patientTook = false),
                        afternoon = Prescription.Medicine.DaySchedule.Schedule(doctorSaid = false, patientTook = true),
                        night = Prescription.Medicine.DaySchedule.Schedule(doctorSaid = true, patientTook = true)
                    )
                )
            )
        )

        // Assert Prescription properties
        assertEquals(1, prescription.appointmentId)
        assertEquals(1001, prescription.prescriptionId)
        assertEquals(1, prescription.medicines?.size)
        assertEquals("10mg", prescription.medicines?.get("Medicine1")?.dosage)
        assertEquals(7, prescription.medicines?.get("Medicine1")?.numberOfDays)
        assertEquals(true, prescription.medicines?.get("Medicine1")?.schedule?.morning?.doctorSaid)
        assertEquals(false, prescription.medicines?.get("Medicine1")?.schedule?.morning?.patientTook)
        assertEquals(false, prescription.medicines?.get("Medicine1")?.schedule?.afternoon?.doctorSaid)
        assertEquals(true, prescription.medicines?.get("Medicine1")?.schedule?.afternoon?.patientTook)
        assertEquals(true, prescription.medicines?.get("Medicine1")?.schedule?.night?.doctorSaid)
        assertEquals(true, prescription.medicines?.get("Medicine1")?.schedule?.night?.patientTook)
    }
}
