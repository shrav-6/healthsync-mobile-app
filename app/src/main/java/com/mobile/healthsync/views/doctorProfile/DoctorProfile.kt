package com.mobile.healthsync.views.doctorProfile

//import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.DoctorRepository


class DoctorProfile : AppCompatActivity() {

    private  lateinit var doctorRepository: DoctorRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

//        var sampleDoctorId = "01lZByxUbILOrFI8XTLq"
        var sampleDoctorId = "QMW1ZsIEcyRjqyLip0dP"
        doctorRepository = DoctorRepository(this)
        doctorRepository.getDoctorProfileData(sampleDoctorId) { doctor ->
            if(doctor != null){
                setDoctorProfileData(doctor)
            }
        }

        val editButton: Button = findViewById(R.id.editDoctor)
        editButton.setOnClickListener{
            val intent = Intent(this, EditDoctorProfile::class.java)
            intent.putExtra("doctorId", sampleDoctorId);
            startActivity(intent)
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

        doctorNameTextView.text = "Dr. ${doctor.doctor_info.name}"
        doctorSpecializationTextView.text = doctor.doctor_info.doctor_speciality
        doctorEmailTextView.text = doctor.email
        doctorAgeTextView.text = "Age: ${doctor.doctor_info.age}"
        doctorGenderTextView.text = "Gender: ${doctor.doctor_info.gender}"
        doctorFeesTextView.text = "Consultation Fees: \$${doctor.doctor_info.consultation_fees}"
        doctorExperienceTextView.text = "Years Of Experience: ${doctor.doctor_info.years_of_practice} years"
        doctorRatingTextView.text = "Average Ratings: ${doctor.doctor_info.avg_ratings} ⭐"
    }
}

