package com.mobile.healthsync.views.signUp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.SignupRepository
import com.mobile.healthsync.views.login.LoginActivity
import java.util.UUID


class SignupDoctorActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_doctor)
        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

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
            val age = findViewById<EditText>(R.id.editTextAge).text.toString().toInt()
            val speciality = findViewById<EditText>(R.id.editTextSpecialization).text.toString()
            val license_no = findViewById<EditText>(R.id.editTextLicenseNumber).text.toString()
            val license_expiry = findViewById<EditText>(R.id.editTextLicenseExpiry).text.toString()
            val years_of_practice = findViewById<EditText>(R.id.editTextExpYears).text.toString().toInt()
            val gender = spinner.selectedItem.toString()
            val consulation_fees = findViewById<EditText>(R.id.editTextConsulationFees).text.toString().toDouble()

            //create doctor object
            val newDoctor = Doctor(
                doctor_id = (Math.abs(UUID.randomUUID().mostSignificantBits) xor UUID.randomUUID().leastSignificantBits).toInt(),
                availability = emptyMap(),
                Doctor.DoctorInfo(
                    age = age,
                    avg_ratings = 0.0,
                    consultation_fees = consulation_fees,
                    gender = gender,
                    license_expiry = license_expiry,
                    license_no = license_no,
                    years_of_practice = years_of_practice,
                    name = name,
                    photo = "null",
                ),
                email = email, password = password, doctor_speciality = speciality, token ="")

            // upload in database
//            val dbObj = uploadToDatabase()
//            dbObj.createDoctor(newDoctor)

            val repo = SignupRepository(this)
            repo.createDoctor(newDoctor, sharedPreferences)
            Toast.makeText(this, "Doctor Registered", Toast.LENGTH_LONG)
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}