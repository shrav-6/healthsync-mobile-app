package com.mobile.healthsync.views.patientProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.repository.PatientRepository
import com.squareup.picasso.Picasso

/**
 * Activity for displaying patient profile information.
 * Allows the patient to view their profile data including name, email, age, gender, height, weight,
 * allergies, reward points, and profile picture.
 */
class PatientProfile : BaseActivity() {

    private lateinit var patientRepository: PatientRepository
    private val PICK_IMAGE_REQUEST = 100
    private var documentID: String = ""
    private var imageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        patientRepository = PatientRepository(this)

        // Retrieve doctor_id from Shared Preferences
        val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val patientDocumentId = sharedPreferences.getString("patient_documentid", "")

        documentID = patientDocumentId.toString()

        patientRepository.getPatientData(patientDocumentId) { patient ->
            if (patient != null) {
                setPatientData(patient)
            }
        }

        val editButton: Button = findViewById(R.id.editPatient)
        editButton.setOnClickListener{
            val intent = Intent(this, EditPatientProfile::class.java)
            intent.putExtra("patientID", patientDocumentId);
            startActivity(intent)
        }

        val uploadButton: Button = findViewById(R.id.uploadPatientImage)
        uploadButton.setOnClickListener{
            selectImage()
        }
    }

    private fun setPatientData(patient: Patient) : Patient {
        val nameTextBox:TextView = findViewById(R.id.patientName)
        val emailTextBox: TextView = findViewById(R.id.patientEmail)
        val pointsTextBox: TextView = findViewById(R.id.patientPoints)
        val ageTextBox:TextView = findViewById(R.id.patientAge)
        val genderTextBox: TextView = findViewById(R.id.patientGender)
        val heightTextBox:TextView = findViewById(R.id.patientHeight)
        val weightTextBox: TextView = findViewById(R.id.patientWeight)
        val imageView: ShapeableImageView = findViewById(R.id.patientProfileImage)
//        val bloodTypeTextBox:TextView = findViewById(R.id.patientBloodType)
        val allergiesTextBox: TextView = findViewById(R.id.patientAllergies)

        nameTextBox.text = patient.patientDetails.name
        emailTextBox.text = patient.email
        pointsTextBox.text = buildString {
            append("Points: ")
            append(patient.rewardPoints.toString())
            append(" \uD83C\uDFC6")
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
            append(" cm")
        }
        weightTextBox.text = buildString {
            append("Weight: ")
            append(patient.patientDetails.weight.toString())
            append(" kg")
        }

        // https://www.geeksforgeeks.org/how-to-use-picasso-image-loader-library-in-android/

        imageURL = patient.patientDetails.photo.toString()
        if (patient.patientDetails.photo == "null") {
            imageView.setImageResource(R.drawable.user)
        } else {
            Picasso.get().load(Uri.parse(patient.patientDetails.photo)).into(imageView)
        }

//        bloodTypeTextBox.text = buildString {
//            append("Blood Type: ")
//            append(patient.patientDetails.bloodType)
//        }

        allergiesTextBox.text = buildString {
            append("Allergies: \n")
            append(patient.patientDetails.allergies)
        }
        return patient
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val imageView: ShapeableImageView = findViewById(R.id.patientProfileImage)
            imageView.setImageURI(imageUri)

            patientRepository.uploadPhotoToStorage(imageURL, imageUri, documentID) {it
                if (!it.isNullOrBlank()) {
                    imageURL = it
                }
            }
        }
    }
}


