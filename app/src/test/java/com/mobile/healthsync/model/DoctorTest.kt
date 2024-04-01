package com.mobile.healthsync.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DoctorTest {

    @Test
    fun testDoctorProperties() {
        // Create a Doctor instance
        val doctor = Doctor(
            doctor_id = 1,
            availability = mapOf("Monday" to Availability()),
            doctor_info = Doctor.DoctorInfo(
                age = 35,
                avg_ratings = 4.5,
                consultation_fees = 50.0,
                gender = "Male",
                license_expiry = "2023-12-31",
                license_no = "12345",
                name = "Dr. John Doe",
                photo = "photo_url",
                years_of_practice = 10
            ),
            email = "doctor@example.com",
            password = "password",
            doctor_speciality = "Cardiology",
            token = "token"
        )

        // Assert Doctor properties
        assertEquals(1, doctor.doctor_id)
        assertEquals("Monday", doctor.availability?.keys?.first())
        assertEquals(35, doctor.doctor_info.age)
        assertEquals(4.5, doctor.doctor_info.avg_ratings, 0.001)
        assertEquals(50.0, doctor.doctor_info.consultation_fees, 0.001)
        assertEquals("Male", doctor.doctor_info.gender)
        assertEquals("2023-12-31", doctor.doctor_info.license_expiry)
        assertEquals("12345", doctor.doctor_info.license_no)
        assertEquals("Dr. John Doe", doctor.doctor_info.name)
        assertEquals("photo_url", doctor.doctor_info.photo)
        assertEquals(10, doctor.doctor_info.years_of_practice)
        assertEquals("doctor@example.com", doctor.email)
        assertEquals("password", doctor.password)
        assertEquals("Cardiology", doctor.doctor_speciality)
        assertEquals("token", doctor.token)
    }
}
