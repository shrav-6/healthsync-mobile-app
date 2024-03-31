package com.mobile.healthsync.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobile.healthsync.model.Patient
import java.util.UUID

/**
 * This document contains code for the patient repository.
 * This helps manage patient data
 */
class PatientRepository(private val context: Context) {

    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    /**
     * Retrieves patient data from Firebase based on the provided patient ID.
     * @param patientId The document ID of the patient to retrieve.
     * @param callback Callback function to handle the retrieved patient data.
     */
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

    /**
     * Updates patient data.
     * @param documentID The ID of the document to update.
     * @param patient The patient object containing updated data.
     */
    fun updatePatientData(documentID: String, patient: Patient?) {
        val patientID = patient?.patient_id.toString()
//        val documentID = getDocumentID()
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

    /**
     * Uploads a photo to Firebase Storage and updates the patient's photo URL.
     * @param oldImageURL The URL of the old image to delete.
     * @param newImageUri The URI of the new image to upload.
     * @param documentID The ID of the document to update.
     * @param callback Callback function to handle the uploaded image URL.
     */
    fun uploadPhotoToStorage(oldImageURL: String, newImageUri: Uri, documentID: String, callback: (String?) -> Unit) {

        // Delete old image to Firebase Storage
        if (oldImageURL != "null") {
            val oldImageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL)
            oldImageReference.delete().addOnSuccessListener {
                // File deleted successfully
                showToast("Old Image deleted successfully")
            }.addOnFailureListener {
                showToast("Failed to delete old image")
            }
        }

        // Upload new image to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("patientProfileImages/${UUID.randomUUID()}")
        imageReference.putFile(newImageUri).addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                // Image uploaded successfully
                showToast("Image uploaded successfully")
                // Get the uploaded image URL
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Now you can save this URL to Firebase Database or use it as needed
                    Log.d("before updatePatientPhoto", "${documentID}, ${imageUrl}")
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

    /**
     * Retrieves patient data from Firebase based on the patient ID.
     * @param patient_id The ID of the patient to retrieve.
     * @param callback Callback function to handle the retrieved patient data.
     */
    fun getPatientWithPatientId(patient_id: Int, callback: (Patient?) -> Unit) {
        db.collection("patients")
            .whereEqualTo("patient_id",patient_id)
            .get()
            .addOnCompleteListener {
                    task: Task<QuerySnapshot> ->
                if(task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val patient = document.toObject(Patient::class.java)
                        callback(patient)
                    }
                }
                else {
                    showToast("Patient not found")
                }
            }
    }

    /**
     * Updates the patient's photo URL in Firebase.
     * @param documentID The ID of the document to update.
     * @param photoURL The URL of the new photo.
     */
    private fun updatePatientPhoto(documentID: String, photoURL: String?) {
        Log.d("updatePatientPhoto", "${documentID}, ${photoURL}")
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

    /**
     * Retrieves the photo URL for a specific patient document from the database.
     *
     * @param documentID The ID of the patient document to retrieve the photo for.
     * @param callback The callback function to handle the retrieved photo URL.
     */
    fun getPhotoForPatient(documentID: String, callback: (String?) -> Unit) {
        db.collection("patients")
            .document(documentID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val imageURL = document.toObject(Patient::class.java)?.patientDetails?.photo
                        callback(imageURL)
                    }
                    callback("null")
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching event data: $exceptionMessage")
                    callback("null")
                }
            }
    }

    /**
     * Displays a toast message.
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}