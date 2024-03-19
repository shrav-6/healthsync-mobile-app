package com.mobile.healthsync.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.DoctorProfile
import java.util.UUID




class DoctorRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getDoctorData(doctorId: String?) {
        // Reference to the "doctors" collection
        db.collection("doctors").document(doctorId!!)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Document found, parse data and populate Doctor object
                        val doctor = document.toObject(DoctorProfile::class.java)
                        doctor?.let { displayDoctorData(it) }
                    } else {
                        showToast("Doctor not found")
                    }
                } else {
                    showToast("Error fetching doctor data: ${task.exception?.message}")
                }
            }
    }

    fun getAllDoctors(callback: (MutableList<DoctorProfile>) -> Unit) {
        // Reference to the "doctors" collection
        db.collection("doctors")
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val doctorsList = mutableListOf<DoctorProfile>()

                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        // Documents found, parse data and add each Doctor object to the list
                        for (document in documents) {
                            val doctor = document.toObject(DoctorProfile::class.java)
                            doctor?.let { doctorsList.add(it) }
                        }
                    }

                    // Invoke the callback with the list of Doctor objects
                    callback(doctorsList)
                } else {
                    showToast("Error fetching doctors: ${task.exception?.message}")
                }
            }
    }

    private fun displayDoctorData(doctor: DoctorProfile) {
        // Use the populated Doctor object as needed
        // For example, you can access doctor's information like:
        val doctorName: String = doctor.doctor_info.name
        val doctorAge: Int = doctor.doctor_info.age
        // ... other properties
    }

    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getDoctorProfileData(doctorId: String?, callback: (DoctorProfile?) -> Unit) {
        db.collection("doctors").document(doctorId!!)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val doctor = document.toObject(DoctorProfile::class.java)
                        callback(doctor)
                    } else {
                        showToast("Doctor not found")
                        callback(null)
                    }
                } else {
                    showToast("Error fetching doctor data: ${task.exception?.message}")
                    callback(null)
                }
            }
    }

    fun updateDoctorData(documentID: String, doctor: DoctorProfile?) {
//        val doctorId = doctor?.doctor_id.toString()
        db.collection("doctors").document(documentID)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        // Document found, parse data and update with Patient object
                        if (doctor != null) {
                            db.collection("doctors").document(documentID).set(doctor)
                            showToast("Doctor Info Update Success")
                        }
                    } else {
                        showToast("Doctor not found")
                    }
                } else {
                    showToast("Error fetching doctor data: ${task.exception?.message}")
                }
            }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, documentID: String, callback: (String?) -> Unit) {
        // Upload image to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("doctorImages/${UUID.randomUUID()}")
        imageReference.putFile(imageUri).addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                // Image uploaded successfully
                showToast("Image uploaded")
                // Get the uploaded image URL
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    updateDoctorImg(documentID, imageUrl)
                    callback(imageUrl)
                }.addOnFailureListener {
                    uploadTask.exception?.message?.let { it1 -> showToast(it1) }
                    callback("")
                }
            } else {
                showToast("Image upload failed")
                callback("")
            }
        }
    }

    private fun updateDoctorImg(documentID: String, photoURL: String?) {
        db.collection("doctors").document(documentID)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        if (!photoURL.isNullOrBlank()) {
                            db.collection("doctors").document(documentID).update("doctor_info.photo", photoURL)
                            showToast("Image updated")
                        }
                    } else {
                        showToast("Doctor not found")
                    }
                } else {
                    showToast("Error fetching doctor: ${task.exception?.message}")
                }
            }
    }

}