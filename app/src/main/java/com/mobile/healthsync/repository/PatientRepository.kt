package com.mobile.healthsync.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobile.healthsync.model.Patient
import java.util.UUID


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

    fun updatePatientData(documentID: String, patient: Patient?) {
        val patientID = patient?.patient_id.toString()
        db.collection("patients").document(documentID)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Document found, parse data and update with Patient object
                        if (patient != null) {
                            db.collection("patients").document(documentID).set(patient)
                            showToast("Patient Info Update Success")
                        }
                    } else {
                        showToast("Patient not found")
                    }
                } else {
                    showToast("Error fetching patient data: ${task.exception?.message}")
                }
            }
    }

    fun uploadPhotoToStorage(imageUri: Uri, documentID: String, callback: (String?) -> Unit) {

        // Upload image to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("images/${UUID.randomUUID()}")
        imageReference.putFile(imageUri).addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                // Image uploaded successfully
                showToast("Image uploaded successfully")
                // Get the uploaded image URL
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Now you can save this URL to Firebase Database or use it as needed
                    showToast(imageUrl)
                    updatePatientPhoto(documentID, imageUrl)
                    callback(imageUrl)
                }.addOnFailureListener {
                    // Handle failures
                    uploadTask.exception?.message?.let { it1 -> showToast(it1) }
                    callback("")
                }
            } else {
                showToast("Failed to upload image")
                callback("")
            }
        }
    }

    private fun updatePatientPhoto(documentID: String, photoURL: String?) {
        db.collection("patients").document(documentID)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Document found, parse data and update with Patient object
                        if (!photoURL.isNullOrBlank()) {
                            db.collection("patients").document(documentID).update("patient_details.photo", photoURL)
                            showToast("Patient Photo Update Success")
                        }
                    } else {
                        showToast("Patient not found")
                    }
                } else {
                    showToast("Error fetching patient: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}