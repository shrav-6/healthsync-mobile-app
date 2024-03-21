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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.AvailabilityAdapter
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository
import com.squareup.picasso.Picasso

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
        doctorSpecializationEditText.setText(doctor.doctor_info.doctor_speciality)
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
        val availabilityAdapter = AvailabilityAdapter(doctor.availability ?: emptyList())
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
        updateDoctor.doctor_info.doctor_speciality = "${doctorSpecializationEditText.text}"
        updateDoctor.email = "${doctorEmailTextView.text}"
        updateDoctor.doctor_info.age = "${doctorAgeEditText.text}".toInt()
        updateDoctor.doctor_info.gender = "${doctorGenderDropdown.selectedItem}"
        updateDoctor.doctor_info.consultation_fees= "${doctorFeesEditText.text}".toDouble()
        updateDoctor.doctor_info.years_of_practice = "${doctorExperienceEditText.text}".toInt()

        // Update doctor availability
        val recyclerView: RecyclerView = findViewById(R.id.availabilityRecyclerView)
        val availabilityAdapter = recyclerView.adapter as AvailabilityAdapter
        val availabilityList = availabilityAdapter.getAvailabilityList()
        for (i in availabilityList.indices) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as AvailabilityAdapter.AvailabilityViewHolder
            val startTimePicker = viewHolder.startTimePicker
            val endTimePicker = viewHolder.endTimePicker
            val checkBox = viewHolder.availabilityCheckbox

            if (checkBox.isChecked) {
                // If checkbox is checked, set start time and end time to null
                availabilityList[i].start_time = "NA"
                availabilityList[i].end_time = "NA"
            } else {
                // If checkbox is unchecked, set start time and end time as usual
                val startTimeHour = if (startTimePicker.hour >= 12) startTimePicker.hour - 12 else startTimePicker.hour
                val startTimeMinute = startTimePicker.minute
                val startTimeAMPM = if (startTimePicker.hour >= 12) "PM" else "AM"
                val startTimeString = String.format("%02d:%02d %s", startTimeHour, startTimeMinute, startTimeAMPM)

                val endTimeHour = if (endTimePicker.hour >= 12) endTimePicker.hour - 12 else endTimePicker.hour
                val endTimeMinute = endTimePicker.minute
                val endTimeAMPM = if (endTimePicker.hour >= 12) "PM" else "AM"
                val endTimeString = String.format("%02d:%02d %s", endTimeHour, endTimeMinute, endTimeAMPM)

                // Validating start time and end time range
                val startTimeInMinutes = startTimeHour * 60 + startTimeMinute
                val endTimeInMinutes = endTimeHour * 60 + endTimeMinute
                // If start time is smaller than end time
                if (startTimeInMinutes < endTimeInMinutes) {
                    availabilityList[i].start_time = startTimeString
                    availabilityList[i].end_time = endTimeString
                } else {
                    availabilityList[i].start_time = "NA"
                    availabilityList[i].end_time = "NA"
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
                    val message = "Start time cannot be greater than end time for $day"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Update the availability list in the Doctor object
        updateDoctor.availability = availabilityList

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