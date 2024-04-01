package com.mobile.healthsync.views.doctorProfile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.BaseActivityForDoctor
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository
import com.squareup.picasso.Picasso

/**
 * Activity to display and manage the profile of a doctor.
 * This activity allows the doctor to view their profile information,
 * edit their profile, and upload a profile image.
 */
class DoctorProfile : BaseActivityForDoctor() {

    private lateinit var documentId: String
    private lateinit var doctorImg: String
    private var imageURL: String = ""

    private  lateinit var doctorRepository: DoctorRepository
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        // Retrieve doctor_id from Shared Preferences
        val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val doctorDocumentId = sharedPreferences.getString("doctor_documentid", "")

        documentId = doctorDocumentId.toString()

        doctorRepository = DoctorRepository(this)
        doctorRepository.getDoctorProfileData(doctorDocumentId) { doctor ->
            if(doctor != null){
                setDoctorProfileData(doctor)
                doctorImg = doctor.doctor_info.photo.toString()
            }
        }

        val editButton: Button = findViewById(R.id.editDoctor)
        editButton.setOnClickListener{
            val intent = Intent(this, EditDoctorProfile::class.java)
            intent.putExtra("doctorId", doctorDocumentId);
            startActivity(intent)
        }

        val uploadImgButton: Button = findViewById(R.id.editDoctorImage)
        uploadImgButton.setOnClickListener {
            selectImage()
        }
    }

    private fun setDoctorProfileData(doctor: Doctor){
        val doctorNameTextView:TextView = findViewById(R.id.doctorName)
        val doctorSpecializationTextView:TextView = findViewById(R.id.doctorSpecialization)
        val doctorEmailTextView:TextView = findViewById(R.id.doctorEmail)
        val doctorAgeTextView:TextView = findViewById(R.id.doctorAge)
        val doctorGenderTextView:TextView = findViewById(R.id.doctorGender)
        val doctorFeesTextView:TextView = findViewById(R.id.doctorFee)
        val doctorExperienceTextView:TextView = findViewById(R.id.doctorExperience)
        val doctorRatingTextView:TextView = findViewById(R.id.doctorRating)
        val doctorImageView: ShapeableImageView = findViewById(R.id.doctorProfileImage)

        doctorNameTextView.text = "Dr. ${doctor.doctor_info.name}"
        doctorSpecializationTextView.text = doctor.doctor_speciality
        doctorEmailTextView.text = doctor.email
        doctorAgeTextView.text = "Age: ${doctor.doctor_info.age}"
        doctorGenderTextView.text = "Gender: ${doctor.doctor_info.gender}"
        doctorFeesTextView.text = "Consultation Fees: \$${doctor.doctor_info.consultation_fees}"
        doctorExperienceTextView.text = "Years Of Experience: ${doctor.doctor_info.years_of_practice} years"
        doctorRatingTextView.text = "Average Ratings: ${doctor.doctor_info.avg_ratings} ⭐"

        // Getting image from firebase
        if (doctor.doctor_info.photo == "null") {
            imageURL = "null"
            doctorImageView.setImageResource(R.drawable.default_doctor_image)
        } else {
            imageURL = doctor.doctor_info.photo.toString()
            Picasso.get().load(Uri.parse(doctor.doctor_info.photo)).into(doctorImageView)
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            val imageView: ShapeableImageView = findViewById(R.id.doctorProfileImage)
            imageView.setImageURI(imageUri)

            imageUri?.let {
                doctorRepository.uploadImageToFirebaseStorage(imageURL, it, documentId) {it
                    if (!it.isNullOrBlank()) {
                        doctorImg = it
                    }
                }
            }
        }
    }
}

