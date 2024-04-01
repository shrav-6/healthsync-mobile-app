package com.mobile.healthsync.views.signUp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Patient.PatientDetails
import com.mobile.healthsync.views.patientDashboard.PatientToDo
import android.content.Context
import android.widget.Toast
import com.mobile.healthsync.repository.SignupRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import com.mobile.healthsync.views.login.LoginActivity


class SignupActivity : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
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

        // get the edit texts
        val emailEditText: EditText = findViewById(R.id.editTextEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val nameEditText: EditText = findViewById(R.id.editTextName)
        val ageEditText: EditText = findViewById(R.id.editTextAge)
        val heightEditText: EditText = findViewById(R.id.editTextHeight)
        val weightEditText: EditText = findViewById(R.id.editTextWeight)
        val allergiesEditText: EditText = findViewById(R.id.editTextAllergies)

        val registerButton: Button = findViewById(R.id.button)
        val registerDoctorButton: Button = findViewById(R.id.doctorRegister)


        // get data from input fields
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()
            val height = heightEditText.text.toString()
            val weight = weightEditText.text.toString()
            val allergies = allergiesEditText.text.toString()
            val gender = spinner.selectedItem.toString()
            Log.d(
                "signup details",
                "$email, $password, $name, $age, $height, $weight, $allergies, $gender"
            )

            // to set validation logic on these values


            // create patient object
            val newPatient = Patient(
                email = email,
                password = password,
                patientCreated = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                patient_id = (Math.abs(UUID.randomUUID().mostSignificantBits) xor UUID.randomUUID().leastSignificantBits).toInt(),
                patientUpdated = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                rewardPoints = 0,
                token = "",
                patientDetails = PatientDetails(
                    age = age.toInt(),
                    allergies = allergies,
                    gender = gender,
                    height = height.toInt(),
                    name = name,
                    photo = "null",
                    weight = weight.toInt()
                )
            )

            // upload in database
//            val dbObj = uploadToDatabase()
//            dbObj.createPatient(newPatient, sharedPreferences)
            val repo = SignupRepository(this)
            repo.createPatient(newPatient, sharedPreferences)

            //for testing to-do
            Log.d("after patient signup","going to patient todo activity")
            Toast.makeText(this, "Patient Registered", Toast.LENGTH_LONG)
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        registerDoctorButton.setOnClickListener {
            intent = Intent(this, SignupDoctorActivity::class.java)
            startActivity(intent)
        }
    }
}