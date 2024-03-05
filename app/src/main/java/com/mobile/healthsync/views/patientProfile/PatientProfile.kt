package com.mobile.healthsync.views.patientProfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.DoctorAdapter
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.repository.DoctorRepository
import com.mobile.healthsync.repository.PatientRepository

class PatientProfile : AppCompatActivity() {

    private lateinit var patientRepository: PatientRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        patientRepository = PatientRepository(this)

        val testId = "00KDbESIgVNTIDzyAP04"
        patientRepository.getPatientData(testId) { patient ->
            if (patient != null) {
                setPatientData(patient)
            }
        }

        val editButton: Button = findViewById(R.id.editPatient)
        editButton.setOnClickListener{
//            Log.d("key", "patientID")
            val intent = Intent(this, EditPatientProfile::class.java)
            intent.putExtra("patientID", testId);
            startActivity(intent)
        }

    }

    private fun setPatientData(patient: Patient) {
        val nameTextBox:TextView = findViewById(R.id.patientName)
        val emailTextBox: TextView = findViewById(R.id.patientEmail)
        val pointsTextBox: TextView = findViewById(R.id.patientPoints)
        val ageTextBox:TextView = findViewById(R.id.patientAge)
        val genderTextBox: TextView = findViewById(R.id.patientGender)
        val heightTextBox:TextView = findViewById(R.id.patientHeight)
        val weightTextBox: TextView = findViewById(R.id.patientWeight)
        val bloodTypeTextBox:TextView = findViewById(R.id.patientBloodType)
        val allergiesTextBox: TextView = findViewById(R.id.patientAllergies)

        nameTextBox.text = patient.patientDetails.name
        emailTextBox.text = patient.email
        pointsTextBox.text = buildString {
            append("Points: ")
            append(patient.rewardPoints.toString())
            append(" \uD83D\uDD36 \uD83D\uDC8E")
        }
        ageTextBox.text = buildString {
            append("Age: ")
            append(patient.patientDetails.age.toString())
        }
        genderTextBox.text = buildString {
            append("Gender: ")
            append(patient.patientDetails.gender)
        }
        heightTextBox.text = buildString {
            append("Height: ")
            append(patient.patientDetails.height.toString())
            append(" m")
        }
        weightTextBox.text = buildString {
            append("Weight: ")
            append(patient.patientDetails.weight.toString())
            append(" kg")
        }
//        bloodTypeTextBox.text = buildString {
//            append("Blood Type: ")
//            append("AB+") // patient.patientDetails.bloodType
//        }
//        allergiesTextBox.text = buildString {
//            append("Allergies: ")
//            append("Dairy, Gluten...") // patient.patientDetails.allergies
//        }
    }


}


