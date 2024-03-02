package com.mobile.healthsync

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.integerArrayResource
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.views.patientDashboard.DoctorInfoActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, DoctorInfoActivity::class.java)
        intent.putExtra("doctor",getDoctor() as Parcelable)
        startActivity(intent)
    }

    fun getDoctor() : Doctor
    {
        val doctor = Doctor(
            doctor_id = 123,
            availability = mapOf(
                "Monday" to mapOf("morning" to "9:00 AM - 12:00 PM", "afternoon" to "2:00 PM - 5:00 PM"),
                "Tuesday" to mapOf("morning" to "9:00 AM - 12:00 PM", "afternoon" to "2:00 PM - 5:00 PM"),
                // Add availability for other days as needed
            ),
            doctor_info = Doctor.DoctorInfo(
                age = 35,
                avg_ratings = 4.5,
                consultation_fees = 100.0,
                gender = "Male",
                license_expiry = "2025-12-31",
                license_no = "123456",
                name = "Dr. John Doe",
                photo = "https://example.com/photo.jpg",
                doctor_speciality = "General Medicine",
                years_of_practice = 10
            ),
            email = "doctor@example.com",
            password = "password123"
        )
        return doctor
    }
}
