package com.mobile.healthsync.views.signUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.DoctorInfo
import com.mobile.healthsync.uploadToDatabase


class SignupDoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_doctor)

        // for gender options spinner
        val spinner: Spinner = findViewById(R.id.gender_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        val registerButton: Button = findViewById(R.id.button)
        registerButton.setOnClickListener {

            // get details from edittexts
            val email = findViewById<EditText>(R.id.editTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val name = findViewById<EditText>(R.id.editTextName).text.toString()
            val age = findViewById<EditText>(R.id.editTextAge).text.toString()
            val speciality = findViewById<EditText>(R.id.editTextSpecialization).text.toString()
            val license_no = findViewById<EditText>(R.id.editTextLicenseNumber).text.toString()
            val license_expiry = findViewById<EditText>(R.id.editTextLicenseExpiry).text.toString()
            val years_of_practice = findViewById<EditText>(R.id.editTextExpYears).text.toString()
            val gender = spinner.selectedItem.toString()
            val consulation_fees = findViewById<EditText>(R.id.editTextConsulationFees).text.toString()

            //create doctor object
            val newDoctor = Doctor(null, emptyList(), DoctorInfo(
                age = age,
                avg_ratings = "0",
                consultation_fees = consulation_fees,
                gender = gender,
                license_expiry = license_expiry,
                license_no = license_no,
                years_of_practice = years_of_practice,
                name = name,
                photo = "null"), speciality,  email, password)

            // upload in database
            val dbObj = uploadToDatabase()
            dbObj.createDoctor(newDoctor)
        }
    }
}