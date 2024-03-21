package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import androidx.activity.ComponentActivity

import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository

class DoctorInfoActivity : ComponentActivity() {

    private var doctorRepository: DoctorRepository

    init {
        doctorRepository = DoctorRepository(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_info)

        var patient_id = intent.extras?.getInt("patient_id", -1) ?: -1
        var doctor_id = intent.extras?.getInt("doctor_id", -1) ?: -1

        //get doctor details:toDo
        doctorRepository.getDoctor(doctor_id, { doctor ->
            // Set doctor details to views
            fillDocotorDetails(doctor)
        })

        // Handle book appointment action
        findViewById<Button>(R.id.btnBookAppointment).setOnClickListener {
            bookAppointment(doctor_id, patient_id)
        }
    }

    private fun fillDocotorDetails(doctor: Doctor?) {
        findViewById<TextView>(R.id.tvDoctorName).text = doctor?.doctor_info?.name
        findViewById<TextView>(R.id.tvSpecialization).text = "Specialization:  ${doctor?.doctor_info?.doctor_speciality}"
        findViewById<TextView>(R.id.tvExperience).text = "Experience: ${doctor?.doctor_info?.years_of_practice} years"
        //find reviews and add them:toDo
        findViewById<TextView>(R.id.tvReviews).text = "Reviews: "

        //add a recyclerView for slots:toDo
        findViewById<TextView>(R.id.tvAvailableSlots).text = "Available Slots: ${doctor?.availability}"
    }

    private fun bookAppointment(doctor_id: Int, patient_id: Int) {
        intent  = Intent(this, BookingInfoActivity::class.java)
        intent.putExtra("doctor_id",doctor_id)
        intent.putExtra("patient_id",patient_id)
        startActivity(intent)
    }
}