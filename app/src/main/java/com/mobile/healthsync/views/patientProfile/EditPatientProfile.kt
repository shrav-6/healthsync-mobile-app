package com.mobile.healthsync.views.patientProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.R
import com.mobile.healthsync.databinding.ActivityMainBinding
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.repository.PatientRepository
import com.squareup.picasso.Picasso
import java.util.Calendar

class EditPatientProfile : AppCompatActivity() {

    private lateinit var patientRepository: PatientRepository
    private val PICK_IMAGE_REQUEST = 100
    private var documentID: String = ""
    private var imageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_patient_profile)

        patientRepository = PatientRepository(this)

        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
        val imageView: ShapeableImageView = findViewById(R.id.patientProfileImage)

        // Get the document id value from the Intent
        val id = intent.getStringExtra("patientID")
        if (id != null) {
            documentID = id
        }

        val patientInfo = Patient()
        patientRepository.getPatientData(documentID) { patient ->
            if (patient != null) {
                patientInfo.patient_id = patient.patient_id
                patientInfo.patientUpdated = patient.patientUpdated
                patientInfo.patientCreated = patient.patientCreated
                patientInfo.patientDetails.name = patient.patientDetails.name
                patientInfo.patientDetails.age = patient.patientDetails.age
                patientInfo.patientDetails.height = patient.patientDetails.height
                patientInfo.patientDetails.weight = patient.patientDetails.weight
                patientInfo.patientDetails.gender = patient.patientDetails.gender
                patientInfo.patientDetails.photo = patient.patientDetails.photo
                imageURL = patient.patientDetails.photo.toString()
                nameEditText.setText(patient.patientDetails.name)
                ageEditText.setText(patient.patientDetails.age.toString())
                heightEditText.setText(patient.patientDetails.height.toString())
                weightEditText.setText(patient.patientDetails.weight.toString())
                Picasso.get().load(Uri.parse(patient.patientDetails.photo)).into(imageView) //https://www.geeksforgeeks.org/how-to-use-picasso-image-loader-library-in-android/
            }
        }

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

        val uploadButton: Button = findViewById(R.id.uploadPatientImage)
        uploadButton.setOnClickListener{
            selectImage()
        }

        val closeButton: Button = findViewById(R.id.closeButton)
        closeButton.setOnClickListener{
            finish()
        }
    }

    private fun getUpdatedInfo(patientID: Int): Patient {
        val nameEditText: EditText = findViewById(R.id.editPatientName)
        val ageEditText: EditText = findViewById(R.id.editPatientAge)
        val heightEditText: EditText = findViewById(R.id.editPatientHeight)
        val weightEditText: EditText = findViewById(R.id.editPatientWeight)
        val genderSelection: Spinner = findViewById(R.id.pickPatientGender)
        val imageView: ShapeableImageView = findViewById(R.id.patientProfileImage)

        val patientDetails = Patient.PatientDetails()

        patientDetails.name = nameEditText.text.toString()
        patientDetails.gender = genderSelection.selectedItem.toString() //oldPatientInfo.patientDetails.gender
        patientDetails.age = ageEditText.text.toString().toInt()
        patientDetails.height = heightEditText.text.toString().toInt()
        patientDetails.weight = weightEditText.text.toString().toInt()
        patientDetails.photo = imageURL

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

//        Log.d(
//            "profile updated details",
//            "${newPatientInfo.patient_id}, ${newPatientInfo.email}, ${newPatientInfo.patientCreated}, ${newPatientInfo.patientUpdated}, ${newPatientInfo.patientDetails.age}, ${newPatientInfo.patientDetails.gender}, ${newPatientInfo.patientDetails.weight}, ${newPatientInfo.patientDetails.height}"
//        )
        return newPatientInfo
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

            patientRepository.uploadPhotoToStorage(imageUri, documentID) {it
                if (!it.isNullOrBlank()) {
                    imageURL = it
                }
            }
        }
    }
}