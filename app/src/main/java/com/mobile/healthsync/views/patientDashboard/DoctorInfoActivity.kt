package com.mobile.healthsync.views.patientDashboard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.mobile.healthsync.R

class DoctorInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_info)

        // Example data fetching method
        val doctorDetails = getDoctorDetails()

        // Set doctor details to views
        findViewById<TextView>(R.id.tvDoctorName).text = doctorDetails.name
        findViewById<TextView>(R.id.tvSpecialization).text = "Specialization: ${doctorDetails.specialization}"
        findViewById<TextView>(R.id.tvExperience).text = "Experience: ${doctorDetails.experience} years"
        findViewById<TextView>(R.id.tvAvailableSlots).text = "Available Slots: ${doctorDetails.availableSlots}"
        findViewById<TextView>(R.id.tvReviews).text = "Reviews: ${doctorDetails.reviews}"

        // Handle book appointment action
        findViewById<Button>(R.id.btnBookAppointment).setOnClickListener {
            bookAppointment(doctorDetails.id)
        }
    }

    private fun getDoctorDetails(): DoctorDetails {
        // Placeholder for fetching doctor details
        // Replace this with actual data fetching logic from your backend or database
        return DoctorDetails("1", "Dr. John Doe", "Cardiology", 10, "10am - 12pm", "★★★★☆")
    }

    private fun bookAppointment(doctorId: String) {
        // Placeholder for booking appointment logic
        // Implement the actual booking logic here, possibly involving a network request
        // For now, we'll just print a log or show a toast
        Toast.makeText(this, "Appointment booked with Doctor ID $doctorId", Toast.LENGTH_SHORT).show()
    }

    data class DoctorDetails(
        val id: String,
        val name: String,
        val specialization: String,
        val experience: Int,
        val availableSlots: String,
        val reviews: String
    )
}