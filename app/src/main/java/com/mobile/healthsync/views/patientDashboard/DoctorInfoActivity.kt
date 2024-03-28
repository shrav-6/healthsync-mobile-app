package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.RatingsAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository
import com.mobile.healthsync.repository.ReviewRepository
import com.mobile.healthsync.views.patientBooking.BookingInfoActivity

class DoctorInfoActivity : ComponentActivity() {

    private var doctorRepository: DoctorRepository
    private var reviewRepository: ReviewRepository = ReviewRepository()

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
        findViewById<Button>(R.id.infoBookAppointmentbtn).setOnClickListener {
            bookAppointment(doctor_id, patient_id)
        }
    }

    private fun fillDocotorDetails(doctor: Doctor?) {
        findViewById<TextView>(R.id.infodoctoctorName).text = doctor?.doctor_info?.name
        findViewById<TextView>(R.id.infoSpecialization).text = "Specialization:  ${doctor?.doctor_speciality}"
        findViewById<TextView>(R.id.infoExperience).text = "Experience: ${doctor?.doctor_info?.years_of_practice} years"

        val availableslots = findViewById<RecyclerView>(R.id.infoAvailableSlots)
        availableslots.layoutManager = GridLayoutManager(this, 3)
        // temp commenting
//        availableslots.adapter = AvailableSlotAdapter(doctor?.availability)

        reviewRepository.getReviews(doctor!!.doctor_id , { reviewlist ->
            val reviews = findViewById<RecyclerView>(R.id.infoReviews)
            reviews.adapter = RatingsAdapter(reviewlist)
            reviews.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        })

    }

    private fun bookAppointment(doctor_id: Int, patient_id: Int) {
        val intent  = Intent(this, BookingInfoActivity::class.java)
        intent.putExtra("doctor_id",doctor_id)
        intent.putExtra("patient_id",patient_id)
        startActivity(intent)
    }
}