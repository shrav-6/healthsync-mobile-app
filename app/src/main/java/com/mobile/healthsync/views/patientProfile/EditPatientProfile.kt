package com.mobile.healthsync.views.patientProfile

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.repository.PatientRepository
import com.squareup.picasso.Picasso
import java.util.Calendar
import java.util.Locale

class EditPatientProfile : AppCompatActivity() {

    private lateinit var patientRepository: PatientRepository
    private var documentID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_patient_profile)

        patientRepository = PatientRepository(this)

        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
        val imageView: ShapeableImageView = findViewById(R.id.patientProfileImage)
        val allergiesEditText: EditText = findViewById(R.id.editPatientAllergies)

        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)
        val arrayAdapter1 = ArrayAdapter.createFromResource(
            this, R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSelection.adapter = adapter
        }

        // Get the document id value from the Intent
        val id = intent.getStringExtra("patientID")
        if (id != null) {
            documentID = id
        }

        val currentPatientInfo = Patient()
        patientRepository.getPatientData(documentID) { patient ->
            if (patient != null) {
                // need to add email, password, points and created date
                currentPatientInfo.patient_id = patient.patient_id
                currentPatientInfo.patientUpdated = patient.patientUpdated
                currentPatientInfo.patientDetails.name = patient.patientDetails.name
                currentPatientInfo.patientDetails.age = patient.patientDetails.age
                currentPatientInfo.patientDetails.height = patient.patientDetails.height
                currentPatientInfo.patientDetails.weight = patient.patientDetails.weight
                currentPatientInfo.patientDetails.gender = patient.patientDetails.gender
                currentPatientInfo.patientDetails.photo = patient.patientDetails.photo
                currentPatientInfo.patientDetails.allergies = patient.patientDetails.allergies

                nameEditText.setText(patient.patientDetails.name)
                ageEditText.setText(patient.patientDetails.age.toString())
                heightEditText.setText(patient.patientDetails.height.toString())
                weightEditText.setText(patient.patientDetails.weight.toString())
                allergiesEditText.setText(patient.patientDetails.allergies)

                genderSelection.setSelection(arrayAdapter1.getPosition(patient.patientDetails.gender))

                if (patient.patientDetails.photo == "null") {
                    imageView.setImageResource(R.drawable.user)
                } else {
                    Picasso.get().load(Uri.parse(patient.patientDetails.photo)).into(imageView)
                }
            }
        }

//        val bloodTypeSelection: Spinner = findViewById(R.id.pickPatientBloodType)
//        val arrayAdapter2 = ArrayAdapter.createFromResource(
//            this, R.array.bloodType_array, android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            bloodTypeSelection.adapter = adapter
//        }
//         bloodTypeSelection.setSelection(arrayAdapter2.getPosition(patientInfo.patientDetails))

        val saveButton: Button = findViewById(R.id.savePatientProfile)
        saveButton.setOnClickListener{
            val updatedPatientInfo = getUpdatedPatientInfo(currentPatientInfo)
            if (id != null) {
                patientRepository.updatePatientData(id, updatedPatientInfo)
            }
            val intent = Intent(this, PatientProfile::class.java)
            val handler = Handler()
            handler.postDelayed({startActivity(intent)}, 1000)
        }

        val closeButton: Button = findViewById(R.id.closeButton)
        closeButton.setOnClickListener{
            finish()
        }
    }

    private fun getUpdatedPatientInfo(patient: Patient): Patient {
        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)
        val allergiesEditText: EditText = findViewById(R.id.editPatientAllergies)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val formattedDate = dateFormat.format(currentDate)

        patient.patientUpdated = formattedDate
        patient.patientDetails.name = nameEditText.text.toString()
        patient.patientDetails.gender = genderSelection.selectedItem.toString()
        patient.patientDetails.age = ageEditText.text.toString().toInt()
        patient.patientDetails.height = heightEditText.text.toString().toInt()
        patient.patientDetails.weight = weightEditText.text.toString().toInt()
        patient.patientDetails.allergies = allergiesEditText.text.toString()

        return patient
    }
}