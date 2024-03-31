package com.mobile.healthsync.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PatientTest {

    @Test
    fun testPatientProperties() {
        // Create a Patient instance
        val patient = Patient(
            email = "patient@example.com",
            password = "password",
            patientCreated = "2022-04-01",
            patientDetails = Patient.PatientDetails(
                age = 30,
                allergies = "Peanuts",
                gender = "Female",
                height = 160,
                weight = 55,
                name = "Jane Doe",
                photo = "photo_url"
            ),
            patient_id = 1,
            patientUpdated = "2022-04-02",
            rewardPoints = 50,
            token = "token"
        )

        // Assert Patient properties
        assertEquals("patient@example.com", patient.email)
        assertEquals("password", patient.password)
        assertEquals("2022-04-01", patient.patientCreated)
        assertEquals(30, patient.patientDetails.age)
        assertEquals("Peanuts", patient.patientDetails.allergies)
        assertEquals("Female", patient.patientDetails.gender)
        assertEquals(160, patient.patientDetails.height)
        assertEquals(55, patient.patientDetails.weight)
        assertEquals("Jane Doe", patient.patientDetails.name)
        assertEquals("photo_url", patient.patientDetails.photo)
        assertEquals(1, patient.patient_id)
        assertEquals("2022-04-02", patient.patientUpdated)
        assertEquals(50, patient.rewardPoints)
        assertEquals("token", patient.token)
    }

}