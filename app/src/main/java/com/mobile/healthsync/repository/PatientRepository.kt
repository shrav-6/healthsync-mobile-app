package com.mobile.healthsync.repository

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Patient


class PatientRepository(private val context: Context) {

    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getPatientData(patientId: String?, callback: (Patient?) -> Unit) {
        // Reference to the "patients" collection
        db.collection("patients").document(patientId!!)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Document found, parse data and populate Patient object
                        val patient = document.toObject(Patient::class.java)
                        callback(patient)
                    } else {
                        showToast("Patient not found")
                        callback(null)
                    }
                } else {
                    showToast("Error fetching patient data: ${task.exception?.message}")
                    callback(null)
                }
            }
    }

    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}