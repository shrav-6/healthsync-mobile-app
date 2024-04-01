package com.mobile.healthsync.views.doctorProfile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.AvailabilityAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository
import com.squareup.picasso.Picasso

/**
 * Activity for editing doctor profile information.
 * Allows the doctor to modify their profile data including personal information, specialization,
 * consultation fees, years of experience, gender, and availability.
 */
class EditDoctorProfile : AppCompatActivity() {

    private  lateinit var doctorRepository: DoctorRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor_profile)

        val id = intent.getStringExtra("doctorId")
        var currentDoctorProfileData = Doctor()
        doctorRepository = DoctorRepository(this)
        doctorRepository.getDoctorProfileData(id) { doctor ->
            if(doctor != null){
                currentDoctorProfileData = displayDoctorProfileData(doctor)
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
            finish()
        }
    }


    private fun displayDoctorProfileData(doctor: Doctor): Doctor {
        val doctorNameEditText: EditText = findViewById(R.id.editDoctorName)
        val doctorSpecializationEditText: EditText = findViewById(R.id.editDoctorSpecialization)
        val doctorEmailTextView: TextView = findViewById(R.id.editDoctorEmail)
        val doctorAgeEditText: EditText = findViewById(R.id.editDoctorAge)
        val doctorGenderDropdown: Spinner = findViewById(R.id.editDoctorGender)
        val doctorFeesEditText: EditText = findViewById(R.id.editDoctorFee)
        val doctorExperienceEditText: EditText = findViewById(R.id.editDoctorExperience)
        val doctorImageView: ShapeableImageView = findViewById(R.id.doctorProfileImage)

        doctorNameEditText.setText(doctor.doctor_info.name)
        doctorSpecializationEditText.setText(doctor.doctor_speciality)
        doctorEmailTextView.text = doctor.email
        doctorAgeEditText.setText(doctor.doctor_info.age.toString())
        doctorFeesEditText.setText(doctor.doctor_info.consultation_fees.toString())
        doctorExperienceEditText.setText(doctor.doctor_info.years_of_practice.toString())
        // Setting the gender value from Firebase
        var genderIndex = getSpinnerIndex("gender", doctor.doctor_info.gender)
        doctorGenderDropdown.setSelection(genderIndex)

        // Getting image from firebase
        if (doctor.doctor_info.photo == "null") {
            doctorImageView.setImageResource(R.drawable.default_doctor_image)
        } else {
            Picasso.get().load(Uri.parse(doctor.doctor_info.photo)).into(doctorImageView)
        }

        //Getting availability from firebase
        val recyclerView: RecyclerView = findViewById(R.id.availabilityRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val availabilityAdapter = doctor.availability?.let { AvailabilityAdapter(it) }
        recyclerView.adapter = availabilityAdapter

        return doctor;
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
        updateDoctor.doctor_speciality = "${doctorSpecializationEditText.text}"
        updateDoctor.email = "${doctorEmailTextView.text}"
        updateDoctor.doctor_info.age = "${doctorAgeEditText.text}".toInt()
        updateDoctor.doctor_info.gender = "${doctorGenderDropdown.selectedItem}"
        updateDoctor.doctor_info.consultation_fees= "${doctorFeesEditText.text}".toDouble()
        updateDoctor.doctor_info.years_of_practice = "${doctorExperienceEditText.text}".toInt()

        // Update doctor availability
        val recyclerView: RecyclerView = findViewById(R.id.availabilityRecyclerView)
        val availabilityAdapter = recyclerView.adapter as AvailabilityAdapter
        val availabilityList = availabilityAdapter.getAvailabilityList()
        for (i in availabilityList.keys.toList().indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? AvailabilityAdapter.AvailabilityViewHolder
            val checkBox = viewHolder?.availabilityCheckbox

            val day = when (i) {
                0 -> "Monday"
                1 -> "Tuesday"
                2 -> "Wednesday"
                3 -> "Thursday"
                4 -> "Friday"
                5 -> "Saturday"
                6 -> "Sunday"
                else -> ""
            }

            if (checkBox != null) {
                // if checkbox is checked, update db to false as doctor is not available
                if (checkBox.isChecked && day!= "") {
                    availabilityList[day]!!.is_available = false;
                } else {
                    availabilityList[day]!!.is_available = true;
                }
            }
        }

        // Update the availability list in the Doctor object
        //making temp commenting
//        updateDoctor.availability = availabilityList

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