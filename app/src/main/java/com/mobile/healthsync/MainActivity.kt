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
        intent.putExtra("patient_id",784)
        intent.putExtra("doctor_id",663)
        startActivity(intent)
    }

    fun getDoctor() : Doctor
    {
        val doctor = Doctor(
            doctor_id = 663,
            availability = listOf(
                mapOf("start_time" to "9:00 AM", "end_time" to "2:00 PM", "slot_id" to "1"),
                mapOf("start_time" to "12:00 PM", "end_time" to "5:00 PM", "slot_id" to "1")),
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
