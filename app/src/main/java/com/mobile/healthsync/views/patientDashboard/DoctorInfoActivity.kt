package com.mobile.healthsync.views.patientDashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.AvailableSlotAdapter
import com.mobile.healthsync.adapters.RatingsAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository
import com.mobile.healthsync.repository.ReviewRepository
import com.mobile.healthsync.views.patientBooking.BookingInfoActivity
import com.squareup.picasso.Picasso

class DoctorInfoActivity : BaseActivity() {

    // Repository instances for doctor and review data
    private var doctorRepository: DoctorRepository
    private var reviewRepository: ReviewRepository = ReviewRepository()

    // Initializing doctor repository
    init {
        doctorRepository = DoctorRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_info)

        // Shared preferences to retrieve patient id
        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        // Retrieving patient id from shared preferences, default is 251
        var patient_id = sharedPreferences.getString("patient_id", "251")?.toInt() ?: 251
        // Retrieving doctor id from intent extras
        var doctor_id = intent.extras?.getInt("doctor_id", -1) ?: -1

        // Fetching doctor details from repository
        doctorRepository.getDoctor(doctor_id, { doctor ->
            // Set doctor details to views
            fillDocotorDetails(doctor)
        })

        // Handle book appointment action
        findViewById<Button>(R.id.infoBookAppointmentbtn).setOnClickListener {
            bookAppointment(doctor_id, patient_id)
        }
    }

    // Function to populate doctor details on the UI
    private fun fillDocotorDetails(doctor: Doctor?) {

        // ImageView for doctor image
        val doctorImage: ImageView = findViewById(R.id.doctorImage)
        // Loading doctor image from URI using Picasso library
        if (doctor?.doctor_info?.photo == "null") {
            doctorImage.setImageResource(R.drawable.user)
        } else {
            Picasso.get().load(Uri.parse(doctor?.doctor_info?.photo)).into(doctorImage)
        }

        // Setting doctor name, specialization, and experience to TextViews
        findViewById<TextView>(R.id.infodoctoctorName).text = doctor?.doctor_info?.name
        findViewById<TextView>(R.id.infoSpecialization).text = "Specialization:  ${doctor?.doctor_speciality}"
        findViewById<TextView>(R.id.infoExperience).text = "Experience: ${doctor?.doctor_info?.years_of_practice} years"

        // RecyclerView for displaying available slots
        val availableslots = findViewById<RecyclerView>(R.id.infoAvailableSlots)
        availableslots.layoutManager = GridLayoutManager(this, 3)
        val availabity_map = doctor?.availability

        // Spinner for selecting weekdays
        val spinner : Spinner = findViewById(R.id.weekday)
        val spinnerAdapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.week_days,android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = spinnerAdapter

        // Handling spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected_day = parent?.getItemAtPosition(position).toString()
                val availability = availabity_map?.get(selected_day)
                // Updating available slots based on selected day
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

        // RecyclerView for displaying doctor reviews and ratings
        reviewRepository.getReviews(doctor!!.doctor_id , { reviewlist ->
            val reviews = findViewById<RecyclerView>(R.id.infoReviews)
            reviews.adapter = RatingsAdapter(reviewlist)
            reviews.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        })

    }

    // Function to handle booking appointment action
    private fun bookAppointment(doctor_id: Int, patient_id: Int) {
        val intent  = Intent(this, BookingInfoActivity::class.java)
        intent.putExtra("doctor_id",doctor_id)
        intent.putExtra("patient_id",patient_id)
        startActivity(intent)
    }
}
