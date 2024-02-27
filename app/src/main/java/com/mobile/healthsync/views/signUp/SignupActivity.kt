package com.mobile.healthsync.views.signUp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.views.signUp.uploadPatientToDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

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
                patientCreated = "2/26/2024",
                patientDetails = mapOf(
                    "age" to age,
                    "allergies" to allergies,
                    "gender" to gender,
                    "height" to height,
                    "name" to name,
                    "photo" to "null"
                )
            )

            // upload in database
            val dbObj = uploadPatientToDatabase()
            dbObj.uploadPatient(newPatient)
        }

        registerDoctorButton.setOnClickListener {
            intent = Intent(this, SignupDoctorActivity::class.java)
            startActivity(intent)
        }
    }


}