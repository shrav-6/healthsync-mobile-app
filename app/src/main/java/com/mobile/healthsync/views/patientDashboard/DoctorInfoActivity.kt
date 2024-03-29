package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.AvailableSlotAdapter
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
        val availabity_map = doctor?.availability

        val spinner : Spinner = findViewById(R.id.weekday)
        val spinnerAdapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.week_days,android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected_day = parent?.getItemAtPosition(position).toString()
                val availability = availabity_map?.get(selected_day)
                if(availability != null && availability?.is_available!!)
                {
                    availableslots.adapter = AvailableSlotAdapter(availability.slots)
                }
                else
                {
                    availableslots.adapter = AvailableSlotAdapter(emptyList())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected if needed
            }
        }

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