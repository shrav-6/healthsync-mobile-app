package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView

import androidx.activity.ComponentActivity

import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor

class DoctorInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_info)
        val doctor = intent.extras?.getParcelable<Doctor>("doctor")

        // Set doctor details to views
        findViewById<TextView>(R.id.tvDoctorName).text = doctor?.doctor_info?.name
        findViewById<TextView>(R.id.tvSpecialization).text = "Specialization:  ${doctor?.doctor_info?.doctor_speciality}"
        findViewById<TextView>(R.id.tvExperience).text = "Experience: ${doctor?.doctor_info?.years_of_practice} years"
        findViewById<TextView>(R.id.tvAvailableSlots).text = "Available Slots: ${doctor?.availability}"
        findViewById<TextView>(R.id.tvReviews).text = "Reviews: "

        // Handle book appointment action
        findViewById<Button>(R.id.btnBookAppointment).setOnClickListener {
            bookAppointment(doctor, patient = 123)
        }
    }

    private fun bookAppointment(doctor: Doctor?, patient: Int) {
        // Placeholder for booking appointment logic
        // Implement the actual booking logic here, possibly involving a network request
        // For now, we'll just print a log or show a toast

        intent  = Intent(this, BookingTestActivity::class.java)
        intent.putExtra("doctor_id",doctor?.doctor_id)
        startActivity(intent)
    }
}