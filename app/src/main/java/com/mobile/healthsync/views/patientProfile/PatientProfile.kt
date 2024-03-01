package com.mobile.healthsync.views.patientProfile

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import com.mobile.healthsync.R

class PatientProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        // Initialize views
        val firstNameEditText:EditText = findViewById(R.id.firstNameEditText)
        val lastNameEditText:EditText = findViewById(R.id.lastNameEditText)
        val weightEditText:EditText = findViewById(R.id.weightEditText)
        val heightNameEditText:EditText = findViewById(R.id.heightEditText)

        // Initialize other views

        // Fetch patient data from Firebase
        fetchPatientData()
    }

    private fun fetchPatientData() {

    }

}


