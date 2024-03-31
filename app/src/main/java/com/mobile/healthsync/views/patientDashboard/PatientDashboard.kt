package com.mobile.healthsync.views.patientDashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.DoctorAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository

class PatientDashboard : BaseActivity() {

    private  lateinit var doctorAdapter: DoctorAdapter
    private  lateinit var doctorRepository: DoctorRepository
    private var doctorsList: MutableList<Doctor> = mutableListOf()
    private var patient_id : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        this.patient_id = sharedPreferences.getString("patient_id", "251")?.toInt() ?: 251
        Log.d("patient_id in PatientDashboard",patient_id.toString())

        //this.patient_id = intent.extras?.getInt("patient_id", 251) ?: 251

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
    fun onRootLayoutClick(view: View) {
        // This method will be called when the root layout is clicked
        // Implement any logic you want to execute when the root layout is clicked
        // For example, you can show a toast message
        Toast.makeText(this, "Root layout clicked", Toast.LENGTH_SHORT).show()
    }
    private fun filterDoctorsList(query: String?): MutableList<Doctor>{
        // Implement your logic to filter the original list based on the query
        var filteredList: MutableList<Doctor> = mutableListOf()

        for(doctor in doctorsList)
        {
            val nameMatch = doctor.doctor_info.name.contains(query.orEmpty(), ignoreCase = true)

            if (nameMatch) {
                // Add the matching doctor to the filtered list
                filteredList.add(doctor)
            } else {
                // If no name match, check for speciality match
                val specialityMatch =
                    doctor.doctor_speciality?.contains(query.orEmpty(), ignoreCase = true) ?: false

                if (specialityMatch) {
                    // Add the matching doctor to the filtered list
                    filteredList.add(doctor)
                }
            }
        }

        return filteredList
    }

    fun updateDoctorsList(newList: MutableList<Doctor>) {
        var rvDoctorList = findViewById<RecyclerView>(R.id.rvDoctorsList)

        doctorAdapter = DoctorAdapter(newList,patient_id,this);

        rvDoctorList.adapter = doctorAdapter
        rvDoctorList.layoutManager = LinearLayoutManager(this)
    }

}