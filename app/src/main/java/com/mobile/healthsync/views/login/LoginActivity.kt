package com.mobile.healthsync.views.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mobile.healthsync.R
import com.mobile.healthsync.views.doctorDashboard.DoctorDashboard
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.signUp.SignupActivity

/**
 * LoginActivity for user authentication.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        // Get references to views
        val emailEditText: EditText = findViewById(R.id.editTextEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val signupButton: Button = findViewById(R.id.signUpButton)
        val doctorLoginButton: Button = findViewById(R.id.doctorLoginButton)
        val patientLoginButton: Button = findViewById(R.id.patientLoginButton)

        emailEditText.text = Editable.Factory.getInstance().newEditable(sharedPreferences.getString("email","default@gmail.com"))
        passwordEditText.text = Editable.Factory.getInstance().newEditable(sharedPreferences.getString("password","password"))
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

    /**
     * Validates the login credentials.
     *
     * @param email The email entered by the user.
     * @param password The password entered by the user.
     * @param userType The type of user logging in (doctor or patient).
     */
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
                            val doctorId = documents.documents.firstOrNull()?.getLong("doctor_id") ?: -1L // Default to -1 if not found
                            val doctorDocumentId = documents.documents.firstOrNull()?.id.toString() ?: ""
                            val doctorInfo = documents.documents.firstOrNull()?.get("doctor_info").toString()
                            val nameIndex = doctorInfo.indexOf("name=")
                            val commaIndex = doctorInfo.indexOf(",", startIndex = nameIndex)
                            val doctorName = doctorInfo.substring(nameIndex + "name=".length, commaIndex)
                            val email = documents.documents.firstOrNull()?.getString("email").toString()
                            val password =  documents.documents.firstOrNull()?.getString("password").toString()
                            // Store doctor_id in Shared Preferences
                            val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                            Log.d("doctorName in Login",doctorName.toString())
                            sharedPreferences.edit().apply {
                                putString("doctor_documentid",doctorDocumentId)
                                putString("doctor_id", doctorId.toString()) // Convert to String and save
                                putBoolean("isDoctor", true)
                                putString("email",email)
                                putString("password",password)
                                putString("doctor_name",doctorName)
                                apply()
                            }

                            generateAndSaveToken(email, userType)
                            startActivity(Intent(this, DoctorDashboard::class.java))
                            Toast.makeText(this, "Doctor login successful", Toast.LENGTH_SHORT).show()
                        }

                        UserType.PATIENT -> {
                            val patientId = documents.documents.firstOrNull()?.getLong("patient_id") ?: -1L // Default to -1 if not found
                            val patientDocumentId = documents.documents.firstOrNull()?.id.toString() ?: ""
                            val email = documents.documents.firstOrNull()?.getString("email").toString()
                            val password =  documents.documents.firstOrNull()?.getString("password").toString()
                            val patientDetails = documents.documents.firstOrNull()?.get("patient_details").toString()
                            val nameIndex = patientDetails.indexOf("name=")
                            val commaIndex = patientDetails.indexOf(",", startIndex = nameIndex)
                            val patientName = patientDetails.substring(nameIndex + "name=".length, commaIndex)
                            Log.d("patient_id in Login",patientId.toString())
                            Log.d("patientName in Login",patientName.toString())

                            // Store doctor_id in Shared Preferences
                            val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                            sharedPreferences.edit().apply {
                                putString("patient_documentid",patientDocumentId)
                                putString("patient_id", patientId.toString()) // Convert to String and save
                                putString("patient_name", patientName.toString())
                                putBoolean("isDoctor", false)
                                putString("email",email)
                                putString("password",password)
                                apply()
                            }
                            generateAndSaveToken(email, userType)
                            startActivity(Intent(this, PatientDashboard::class.java))
                            Toast.makeText(this, "Patient login successful", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.d("LoginActivity", "User not found")
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error searching user: $e")
                Toast.makeText(this, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Generates and saves the token for a user.
     *
     * @param email The email of the user.
     * @param userType The type of user (doctor or patient).
     */
    private fun generateAndSaveToken(email: String, userType: UserType) {
        val tokenTask = FirebaseMessaging.getInstance().token
        tokenTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("LoginActivity", "FirebaseMessaging: Token: $token")
                saveTokenToFirebase(email, token, userType)
            } else {
                Log.e("LoginActivity", "FirebaseMessaging: Failed to get token: ${task.exception}")
            }
        }
    }

    /**
     * Saves the token to Firebase Firestore.
     *
     * @param email The email of the user.
     * @param token The token generated for the user.
     * @param userType The type of user (doctor or patient).
     */
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
                    // Update token in user instance
                    userCollection.document(userId).update("token", token)
                        .addOnSuccessListener {
                            Log.d("LoginActivity", "Token added to user instance successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Error adding token to user instance: $e")
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


    // Enum to represent user types
    enum class UserType {
        DOCTOR,
        PATIENT
    }
}
