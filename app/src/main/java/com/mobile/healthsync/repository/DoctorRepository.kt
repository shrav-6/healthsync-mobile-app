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
import com.mobile.healthsync.model.Availability
import com.mobile.healthsync.model.Doctor
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
                        val doctor = document.toObject(Doctor::class.java)
                        doctor?.let { displayDoctorData(it) }
                    } else {
                        showToast("Doctor not found")
                    }
                } else {
                    showToast("Error fetching doctor data: ${task.exception?.message}")
                }
            }
    }

    fun getDoctor(doctor_id: Int, callback: (Doctor?) -> Unit) {
        db.collection("doctors")
            .whereEqualTo("doctor_id",doctor_id)
            .get()
            .addOnCompleteListener {
                    task: Task<QuerySnapshot> ->
                if(task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val doctor = document.toObject(Doctor::class.java)
                        callback(doctor)
                    }
                }
                else {
                    showToast("Doctor not found")
                }
            }
    }

    fun getDoctorAvailability(doctor_id: Int, callback: (MutableMap<String,Availability>) -> Unit)
    {
        var availabilityMap = mutableMapOf<String,Availability>()
        db.collection("doctors")
            .whereEqualTo("doctor_id", doctor_id)
            .get()
            .addOnCompleteListener{
                    task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        val doctor = documents.documents[0].toObject(Doctor::class.java)
                        doctor?.availability?.forEach { (day, availability) ->
                            if(day is String && availability is Availability)
                            {
                                availabilityMap.put(day,availability)
                            }
                        }
                        callback(availabilityMap)
                    }
                }
            }
    }

    fun getAllDoctors(callback: (MutableList<Doctor>) -> Unit) {
        // Reference to the "doctors" collection
        db.collection("doctors")
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val doctorsList = mutableListOf<Doctor>()

                    val documents = task.result
                    documents?.let {
                        // Documents found, parse data and add each Doctor object to the list
                        for (document in it) {
                            val doctor = document.toObject(Doctor::class.java)
                            doctor?.let { doc -> doctorsList.add(doc) }
                        }
                        // Invoke the callback with the list of Doctor objects
                        callback(doctorsList)
                    } ?: showToast("No documents found.")
                } else {
                    // Handle the task failure
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching doctors: $errorMessage")
                }
            }
    }


    private fun displayDoctorData(doctor: Doctor) {
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

    fun getDoctorProfileData(doctorIdValue: Int?, callback: (Doctor?) -> Unit) {
        db.collection("doctors")
            .whereEqualTo("doctor_id", doctorIdValue)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val doctor = document.toObject(Doctor::class.java)
                        callback(doctor)
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching event data: $exceptionMessage")
                    callback(null)
                }
            }
//        db.collection("doctors").document(doctorId!!)
//            .get()
//            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
//                if (task.isSuccessful) {
//                    val document = task.result
//                    if (document.exists()) {
//                        val doctor = document.toObject(Doctor::class.java)
//                        callback(doctor)
//                    } else {
//                        showToast("Doctor not found")
//                        callback(null)
//                    }
//                } else {
//                    showToast("Error fetching doctor data: ${task.exception?.message}")
//                    callback(null)
//                }
//            }

    }

    fun getDocumentIdByDoctorId(doctorIDKey: String, doctorIDValue: Int, callback: (String?) -> Unit) {
        db.collection("doctors")
            .whereEqualTo(doctorIDKey, doctorIDValue)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        callback(document.id)
                    } else {
                        // If no documents are found with the specified field value
                        callback(null)
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching document ID: $exceptionMessage")
                    callback(null)
                }
            }
    }


    fun updateDoctorData(doctorIdValue: Int?, doctor: Doctor?) {
        val doctorId = doctor?.doctor_id.toString()
        db.collection("doctors")
            .whereEqualTo("doctor_id", doctorIdValue)
            .limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        if (document["doctor_id"] == doctorIdValue) {
                            // Update the specific document
                            if (doctor != null) {
                                db.collection("doctors").document(document.id).set(doctor)
                            }
                        }
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching event data: $exceptionMessage")
                }
            }
//        val doctorId = doctor?.doctor_id.toString()
//        db.collection("doctors").document(documentID)
//            .get()
//            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
//                if (task.isSuccessful) {
//                    val document = task.result
//                    if (document.exists()) {
//                        // Document found, parse data and update with Patient object
//                        if (doctor != null) {
//                            db.collection("doctors").document(documentID).set(doctor)
//                            showToast("Doctor Info Update Success")
//                        }
//                    } else {
//                        showToast("Doctor not found")
//                    }
//                } else {
//                    showToast("Error fetching doctor data: ${task.exception?.message}")
//                }
//            }
    }

    fun uploadImageToFirebaseStorage(oldImageURL: String, imageUri: Uri, documentID: Int, callback: (String?) -> Unit) {

        // Delete old image to Firebase Storage
        if (oldImageURL != "null") {
            val oldImageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL)
            oldImageReference.delete().addOnSuccessListener {
                Log.d("Delete old image from db","Old Image deleted successfully")
            }.addOnFailureListener {
                Log.d("Delete old image from db","Failed to delete old image")
            }
        }

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

    private fun updateDoctorImg(doctorIdValue: Int, photoURL: String?) {
        db.collection("doctors")
            .whereEqualTo("doctor_id", doctorIdValue)
            .limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        if (document["doctor_id"] == doctorIdValue) {
                            // Update the specific document
                            if (!photoURL.isNullOrBlank()) {
                                db.collection("doctors").document(document.id).update("doctor_info.photo", photoURL)
                                showToast("Image updated")
                            }
                        }
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching event data: $exceptionMessage")
                }
            }
//        db.collection("doctors")
//            .document(documentID)
//            .get()
//            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
//                if (task.isSuccessful) {
//                    val document = task.result
//                    if (document.exists()) {
//                        if (!photoURL.isNullOrBlank()) {
//                            db.collection("doctors").document(documentID).update("doctor_info.photo", photoURL)
//                            showToast("Image updated")
//                        }
//                    } else {
//                        showToast("Doctor not found")
//                    }
//                } else {
//                    showToast("Error fetching doctor: ${task.exception?.message}")
//                }
//            }
    }

}