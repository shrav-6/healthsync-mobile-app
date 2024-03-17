package com.mobile.healthsync.views.patientProfile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
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

        patientRepository = PatientRepository(this)
        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)

        // Get the value from the Intent
        val id = intent.getStringExtra("patientID")
        Log.d("observationkey", "$id")

        val patientInfo = Patient()
        patientRepository.getPatientData(id) { patient ->
            if (patient != null) {
                patientInfo.patient_id = patient.patient_id
                patientInfo.patientUpdated = patient.patientUpdated
                patientInfo.patientCreated = patient.patientCreated
                patientInfo.patientDetails.name = patient.patientDetails.name
                patientInfo.patientDetails.age = patient.patientDetails.age
                patientInfo.patientDetails.height = patient.patientDetails.height
                patientInfo.patientDetails.weight = patient.patientDetails.weight
                patientInfo.patientDetails.gender = patient.patientDetails.gender
                nameEditText.setText(patient.patientDetails.name)
                ageEditText.setText(patient.patientDetails.age.toString())
                heightEditText.setText(patient.patientDetails.height.toString())
                weightEditText.setText(patient.patientDetails.weight.toString())
            }
        }

//        nameEditText.setText(patientInfo.patientDetails.name)
//        ageEditText.setText(patientInfo.patientDetails.age.toString())
//        heightEditText.setText(patientInfo.patientDetails.height.toString())
//        weightEditText.setText(patientInfo.patientDetails.weight.toString())


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
//        genderSelection.setSelection(patientInfo.patientDetails.gender)

        val saveButton: Button = findViewById(R.id.savePatientProfile)
        saveButton.setOnClickListener{
            val updatedPatientInfo = getUpdatedInfo(patientInfo.patient_id)
            if (id != null) {
                patientRepository.updatePatientData(id, updatedPatientInfo)
            }
            val intent = Intent(this, PatientProfile::class.java)
            val handler = Handler()
            handler.postDelayed({startActivity(intent)}, 1000)
        }
    }

    private fun getUpdatedInfo(patientID: Int): Patient {
        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)

        val patientDetails = Patient.PatientDetails()

        patientDetails.name = nameEditText.text.toString()
        patientDetails.gender = genderSelection.selectedItem.toString() //oldPatientInfo.patientDetails.gender
        patientDetails.age = ageEditText.text.toString().toInt()
        patientDetails.height = heightEditText.text.toString().toInt()
        patientDetails.weight = weightEditText.text.toString().toInt()
        patientDetails.allergies = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAALvSURBVDjLZZJdSFNhGMf/5+zo2pyb5cdyxXRNW2WkhhJGXlReFEZEBV0UVARBIAiCXnojdFE3QUQR3k6iC6GIoK+LMDNBaVpOW04by/yYzuk8O9v57DnHJqteeM57zvue5/c+///zMpqmITv6+vpsqqp2KorSRLGDAhRxiiFZlu+2t7dv4J/BZAF+v7+OkvtdLpfHbreDZVnQN9LpNGKxGGZpEOh8V1dX4D8AJdto87PX660SRRHRaBQ8z+ung+M4OJ1O4+dgMDhNa4e6u7uFLIDTH7R4q7y8vEqSJIRCoRkq9wSt/dIBgiC4EonER4/H46qtFKqqmXBq+vlt8MvvwaTnrhoASmiyWq0Ih8MgyJm2trZITpWRnp6eFmbtbbChuhiWkitweOqRmPVh6nXvnSygVNecTCb199l/jbpc56+3ey7BXtSAeHgS+YyIQvtO2IrdDiYycF0bCvuwuGYxNJ+tGYFJk6ApMjRZJpPWUVTVDMeeU8jMP4GwwmDpWwpSWlxJCxtHOZCJFy8cBwMWjMlC82lAZcidbUjFhpFJBODwtiI99whsvow8WwXM/BhSfH5LY8ebEKefBGiQl5+CM5eAYWwEyMPCHClhVJQdPEfJD8HmyRDXPVgZHEWaX8LhjkmjnaxeJlS6C4qIxMQoEsERLEQmsRrPoKymFeJCL0z5GjLrFYgNfILz5DWoUmrLHwJI0GVoioQi314siSziCQskzY35L/dBVwl8fBeWB4ex3cuAK7BDk8QcAPVe0xSqQMLq1wDGxn/gwLGbMEc/IPRsEIFXcUy9fAfWtAaWU6laFXrOXwBotEgSiqor8X1mEeLEC3hqm1FQQN0Zn4LviJtOL6auiIbcXABnlENUVdY9mMBEaB73Hj9A475KWEvNaNrvIx9+QuKTKHRT+STKkJ0L0CWYd9+ApcIEf4vZaCHZTmCSJgpQhCQpzFChyqZfuvFbADGDmf5Ooyx9Q6dvhrw10w3bvFiKsvmug/6M39LTvtXHnYlaAAAAAElFTkSuQmCC"

        val newPatientInfo = Patient(
            patient_id = patientID,
            password = "eH2?qsHr)d",
            email = "rb618118@dal.ca",
            patientCreated = "7/31/2023",
            rewardPoints = 40,
            patientUpdated = Calendar.getInstance().toString(),
            patientDetails = patientDetails
        )

        Log.d(
            "profile updated details",
            "${newPatientInfo.patient_id}, ${newPatientInfo.email}, ${newPatientInfo.patientCreated}, ${newPatientInfo.patientUpdated}, ${newPatientInfo.patientDetails.age}, ${newPatientInfo.patientDetails.gender}, ${newPatientInfo.patientDetails.weight}, ${newPatientInfo.patientDetails.height}"
        )
        return newPatientInfo
    }

}