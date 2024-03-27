package com.mobile.healthsync.views.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.healthsync.R
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.signUp.SignupActivity
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Get references to views
        val emailEditText: EditText = findViewById(R.id.editTextEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val signupButton: Button = findViewById(R.id.signUpButton)
        val doctorLoginButton: Button = findViewById(R.id.doctorLoginButton)
        val patientLoginButton: Button = findViewById(R.id.patientLoginButton)

        // For doctor login
        doctorLoginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            validateLogin(email, password, UserType.DOCTOR)
        }

        // For patient login
        patientLoginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            validateLogin(email, password, UserType.PATIENT)
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateLogin(email: String, password: String, userType: UserType) {
        val db = FirebaseFirestore.getInstance()

        // Get the appropriate collection based on user type
        val collection = when(userType) {
            UserType.DOCTOR -> db.collection("doctors")
            UserType.PATIENT -> db.collection("patients")
        }

        collection.whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Log.d("UserLogin", "User found")
                    // Handle successful login
                    when(userType) {
                        UserType.DOCTOR -> {
                            generateAndSaveToken(email, userType)
                            // Start Doctor Dashboard
                        }

                        UserType.PATIENT -> {
                            generateAndSaveToken(email, userType)
                            startActivity(Intent(this, PatientDashboard::class.java))
                        }
                    }
                } else {
                    Log.d("UserLogin", "User not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserLogin", "Error searching user: $e")
            }
    }

    private fun generateAndSaveToken(email: String, userType: UserType) {
        val tokenTask = FirebaseMessaging.getInstance().token
        tokenTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FirebaseMessaging", "Token: $token")
                saveTokenToFirebase(email, token, userType)
            } else {
                Log.e("FirebaseMessaging", "Failed to get token: ${task.exception}")
            }
        }
    }

    private fun saveTokenToFirebase(email: String, token: String, userType: UserType) {
        val db = FirebaseFirestore.getInstance()
        val collection = when (userType) {
            UserType.DOCTOR -> db.collection("doctors")
            UserType.PATIENT -> db.collection("patients")
        }

        // Assuming you have a field named "token" in your FirebaseFirestore documents
        collection.document(email).update("token", token)
            .addOnSuccessListener {
                Log.d(
                    "LoginActivity",
                    "Token added to ${userType.name.lowercase()} instance successfully"
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    "LoginActivity",
                    "Error adding token to ${userType.name.lowercase()} instance: $e"
                )
            }
    }

    enum class UserType {
        DOCTOR,
        PATIENT
    }
}
