package com.mobile.healthsync.views.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.healthsync.R
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
                            Toast.makeText(this, "Doctor login successful", Toast.LENGTH_SHORT).show()
                        }

                        UserType.PATIENT -> {
                            generateAndSaveToken(email, userType)
                            startActivity(Intent(this, PatientDashboard::class.java))
                            Toast.makeText(this, "Patient login successful", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("UserLogin", "User not found")
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserLogin", "Error searching user: $e")
                Toast.makeText(this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
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

        val userCollection = when (userType) {
            UserType.DOCTOR -> db.collection("doctors")
            UserType.PATIENT -> db.collection("patients")
        }

        Log.d("LoginActivity", "Fetching user document for email: $email")
        // Get the user document based on email
        userCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents[0]
                    val userId = userDocument.id
                    userCollection.document(userId).update("token", token)
                        .addOnSuccessListener {
                            Log.d("MainActivity", "Token added to user instance successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("MainActivity", "Error adding token to user instance: $e")
                        }
                    Log.d("LoginActivity", "User document found for email: $email")

                    // If you want to add an access token document, uncomment the following lines
                    /*
                    val accessTokensCollection = db.collection("accesstokens")
                    val accessTokenData = hashMapOf(
                        "userId" to userId,
                        "token" to token
                    )
                    accessTokensCollection.add(accessTokenData)
                        .addOnSuccessListener {
                            Log.d("LoginActivity", "Access token document added successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Error adding access token document: $e")
                        }
                    */
                }
            }
    }


    enum class UserType {
        DOCTOR,
        PATIENT
    }
}
