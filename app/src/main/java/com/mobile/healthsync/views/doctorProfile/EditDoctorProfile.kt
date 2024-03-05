package com.mobile.healthsync.views.doctorProfile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository

class EditDoctorProfile : AppCompatActivity() {

    private  lateinit var doctorRepository: DoctorRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor_profile)

        var sampleDoctorId = "QMW1ZsIEcyRjqyLip0dP"
        doctorRepository = DoctorRepository(this)
        doctorRepository.getDoctorProfileData(sampleDoctorId) { doctor ->
            if(doctor != null){
                displayDoctorProfileData(doctor)
            }
        }

        //Pick Doctor Gender
        val genderSelection: Spinner = findViewById(R.id.editDoctorGender)
        ArrayAdapter.createFromResource(
            this, R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSelection.adapter = adapter
        }

        val id = intent.getStringExtra("doctorId")

        val currentDoctorProfileData = Doctor()
        doctorRepository.getDoctorProfileData(id) { doctor ->
            if (doctor != null) {
                currentDoctorProfileData.doctor_id = doctor.doctor_id
                currentDoctorProfileData.availability = doctor.availability
                currentDoctorProfileData.password = doctor.password
                currentDoctorProfileData.doctor_info.avg_ratings = doctor.doctor_info.avg_ratings
                currentDoctorProfileData.doctor_info.license_no = doctor.doctor_info.license_no
                currentDoctorProfileData.doctor_info.license_expiry = doctor.doctor_info.license_expiry
                currentDoctorProfileData.doctor_info.photo = doctor.doctor_info.photo
            }
        }

        // Save changes
        val saveButton: Button = findViewById(R.id.saveDoctorProfile)
        saveButton.setOnClickListener{
            Log.d("id", "$id")
            val updatedDoctorData = getUpdatedDoctorInfo(currentDoctorProfileData)
            if(id != null){
                doctorRepository.updateDoctorData(id, updatedDoctorData)
            }
            val intent = Intent(this, DoctorProfile::class.java)
            // Giving time for firebase to update
            val handler = Handler()
            handler.postDelayed({
                startActivity(intent)
            }, 1000)
        }

        // Close Edit Page
        val closeButton: Button = findViewById(R.id.closeEditPage)
        closeButton.setOnClickListener{
            val intent = Intent(this, DoctorProfile::class.java)
            startActivity(intent)
        }
    }

    private fun displayDoctorProfileData(doctor: Doctor){
        val doctorNameEditText: EditText = findViewById(R.id.editDoctorName)
        val doctorSpecializationEditText: EditText = findViewById(R.id.editDoctorSpecialization)
        val doctorEmailTextView: TextView = findViewById(R.id.editDoctorEmail)
        val doctorAgeEditText: EditText = findViewById(R.id.editDoctorAge)
        val doctorGenderDropdown: Spinner = findViewById(R.id.editDoctorGender)
        val doctorFeesEditText: EditText = findViewById(R.id.editDoctorFee)
        val doctorExperienceEditText: EditText = findViewById(R.id.editDoctorExperience)

        doctorNameEditText.setText(doctor.doctor_info.name)
        doctorSpecializationEditText.setText(doctor.doctor_info.doctor_speciality)
        doctorEmailTextView.text = doctor.email
        doctorAgeEditText.setText(doctor.doctor_info.age.toString())
        doctorFeesEditText.setText(doctor.doctor_info.consultation_fees.toString())
        doctorExperienceEditText.setText(doctor.doctor_info.years_of_practice.toString())
        // Setting the gender value from Firebase
        var genderIndex = getSpinnerIndex("gender", doctor.doctor_info.gender)
        doctorGenderDropdown.setSelection(genderIndex)
    }

    private fun getUpdatedDoctorInfo(updateDoctor: Doctor): Doctor {
        val doctorNameEditText: EditText = findViewById(R.id.editDoctorName)
        val doctorSpecializationEditText: EditText = findViewById(R.id.editDoctorSpecialization)
        val doctorEmailTextView: TextView = findViewById(R.id.editDoctorEmail)
        val doctorAgeEditText: EditText = findViewById(R.id.editDoctorAge)
        val doctorGenderDropdown: Spinner = findViewById(R.id.editDoctorGender)
        val doctorFeesEditText: EditText = findViewById(R.id.editDoctorFee)
        val doctorExperienceEditText: EditText = findViewById(R.id.editDoctorExperience)

        updateDoctor.doctor_info.name = "${doctorNameEditText.text}"
        updateDoctor.doctor_info.doctor_speciality = "${doctorSpecializationEditText.text}"
        updateDoctor.email = "${doctorEmailTextView.text}"
        updateDoctor.doctor_info.age = "${doctorAgeEditText.text}".toInt()
        updateDoctor.doctor_info.gender = "${doctorGenderDropdown.selectedItem}"
        updateDoctor.doctor_info.consultation_fees= "${doctorFeesEditText.text}".toDouble()
        updateDoctor.doctor_info.years_of_practice = "${doctorExperienceEditText.text}".toInt()

        return updateDoctor
    }

    private fun getSpinnerIndex(itemType: String, item: String): Int{
        var index: Int
        ArrayAdapter.createFromResource(
            this, R.array.gender_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            index = adapter.getPosition(item)
            if(itemType == "gender" && index == -1){
                index = adapter.getPosition("Others")
            }
        }
        return index
    }


}