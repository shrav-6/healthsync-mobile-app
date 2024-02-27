package com.mobile.healthsync.views.patientDashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.DoctorAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository

class PatientDashboard : AppCompatActivity() {

    private  lateinit var doctorAdapter: DoctorAdapter
    private  lateinit var doctorRepository: DoctorRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        doctorRepository = DoctorRepository(this)
        var doctorsList : MutableList<Doctor> = mutableListOf()

        doctorRepository.getAllDoctors { retrievedDoctorsList ->
            doctorsList = retrievedDoctorsList

            var rvDoctorList = findViewById<RecyclerView>(R.id.rvDoctorsList)

            doctorAdapter = DoctorAdapter(doctorsList);

            rvDoctorList.adapter = doctorAdapter
            rvDoctorList.layoutManager = LinearLayoutManager(this)

            Log.d("DoctorData", "Number of Doctors: ${doctorsList.size}")
        }


    }


}