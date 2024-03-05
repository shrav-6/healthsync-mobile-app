package com.mobile.healthsync.views.patientProfile

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.repository.PatientRepository
import java.util.Calendar

class EditPatientProfile : AppCompatActivity() {

    private lateinit var patientRepository: PatientRepository
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_patient_profile)

        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)
        ArrayAdapter.createFromResource(
            this, R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSelection.adapter = adapter
        }

        val bloodTypeSelection: Spinner = findViewById(R.id.pickPatientBloodType)
        ArrayAdapter.createFromResource(
            this, R.array.bloodType_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bloodTypeSelection.adapter = adapter
        }

        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)

        // Get the value from the Intent
        val patientID = intent.getStringExtra("patientID")
        Log.d("key", "$patientID")

//        patientRepository.getPatientData(patientID) { patient ->
//            if (patient != null) {
//                nameEditText.setText(patient.patientDetails.name)
//                ageEditText.setText(patient.patientDetails.age)
//                heightEditText.setText(patient.patientDetails.height)
//                weightEditText.setText(patient.patientDetails.weight)
////                genderSelection.setSelection(patient.patientDetails.gender)
//                val updatedPatientInfo = getUpdatedInfo(patient, patient.patient_id)
//                patientRepository.updatePatientData(updatedPatientInfo)
//            }
//        }
    }

//    private fun getUpdatedInfo(oldPatientInfo: Patient, patientID: Int): Patient {
//
//        val nameEditText: EditText = findViewById(R.id.editPatientName)
//        val ageEditText: EditText = findViewById(R.id.editPatientAge)
//        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
//        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
//        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)
//
//        val patientDetails = Patient.PatientDetails()
//
//        patientDetails.name = nameEditText.text.toString()
//        patientDetails.gender =
//            genderSelection.selectedItem.toString() //oldPatientInfo.patientDetails.gender
//        patientDetails.age = ageEditText.text.toString().toInt()
//        patientDetails.height = heightEditText.text.toString().toInt()
//        patientDetails.weight = weightEditText.text.toString().toInt()
//        patientDetails.allergies = oldPatientInfo.patientDetails.allergies
//
//        val newPatientInfo = Patient(
//            patient_id = patientID,
//            patientUpdated = Calendar.getInstance().toString(),
//            patientDetails = patientDetails
//        )
//
//        Log.d(
//            "profile updated details",
//            "${newPatientInfo.patient_id}, ${newPatientInfo.email}, ${newPatientInfo.patientCreated}, ${newPatientInfo.patientUpdated}, ${newPatientInfo.patientDetails.age}, ${newPatientInfo.patientDetails.gender}, ${newPatientInfo.patientDetails.weight}, ${newPatientInfo.patientDetails.height}"
//        )
//
//        return newPatientInfo
//    }

}