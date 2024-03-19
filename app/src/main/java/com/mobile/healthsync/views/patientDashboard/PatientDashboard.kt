package com.mobile.healthsync.views.patientDashboard

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.DoctorAdapter
import com.mobile.healthsync.model.DoctorProfile
import com.mobile.healthsync.repository.DoctorRepository

class PatientDashboard : AppCompatActivity() {

    private  lateinit var doctorAdapter: DoctorAdapter
    private  lateinit var doctorRepository: DoctorRepository
    private var doctorsList: MutableList<DoctorProfile> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        doctorRepository = DoctorRepository(this)

        doctorRepository.getAllDoctors { retrievedDoctorsList ->
            doctorsList = retrievedDoctorsList

            updateDoctorsList(doctorsList)
        }


        val svSearchBox = findViewById<SearchView>(R.id.svSearchBox)

        svSearchBox.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("OriginalDoctorData", "Number of Doctors: ${doctorsList.size}")

                val filteredList = filterDoctorsList(newText)
                updateDoctorsList(filteredList)


                Log.d("FilteredDoctorData", "Number of Doctors: ${filteredList.size}")
                return true
            }
        })
    }

    private fun filterDoctorsList(query: String?): MutableList<DoctorProfile>{
        // Implement your logic to filter the original list based on the query
        var filteredList: MutableList<DoctorProfile> = mutableListOf()

        for(doctor in doctorsList)
        {
            val nameMatch = doctor.doctor_info.name.contains(query.orEmpty(), ignoreCase = true)
            // val specialityMatch = doctor.doctor_info.doctor_speciality?.contains(query.orEmpty(), ignoreCase = true) ?: false

            if (nameMatch /*|| specialityMatch*/) {
                // Add the matching doctor to the filtered list
                filteredList.add(doctor)
            }
        }

        return filteredList
    }

    fun updateDoctorsList(newList: MutableList<DoctorProfile>) {
        var rvDoctorList = findViewById<RecyclerView>(R.id.rvDoctorsList)

        doctorAdapter = DoctorAdapter(newList);

        rvDoctorList.adapter = doctorAdapter
        rvDoctorList.layoutManager = LinearLayoutManager(this)
    }

}