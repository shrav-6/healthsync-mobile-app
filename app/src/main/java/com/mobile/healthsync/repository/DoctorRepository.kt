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

/**
 * Repository class for managing doctor data.
 */
class DoctorRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    /**
     * Retrieves doctor data from Firebase based on the provided doctor ID.
     * @param doctorId The document ID of the doctor to retrieve.
     */
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

    /**
     * Retrieves a doctor from Firebase based on the provided doctor ID.
     * @param doctor_id The ID of the doctor to retrieve.
     * @param callback Callback function to handle the retrieved doctor data.
     */
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

    /**
     * Retrieves the availability of a doctor from Firebase based on the provided doctor ID.
     * @param doctor_id The ID of the doctor.
     * @param callback Callback function to handle the retrieved availability data.
     */
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

    /**
     * Retrieves all doctors from Firebase.
     * @param callback Callback function to handle the retrieved all the doctors.
     */
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


    /**
     * Displays doctor data.
     * @param doctor The doctor object.
     */
    private fun displayDoctorData(doctor: Doctor) {
        val doctorName: String = doctor.doctor_info.name
        val doctorAge: Int = doctor.doctor_info.age
    }

    /**
     * Displays a toast message.
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Retrieves doctor profile data from Firebase based on the provided doctor ID.
     * @param doctorId The document ID of the doctor to retrieve.
     * @param callback Callback function to handle the retrieved doctor data.
     */
    fun getDoctorProfileData(doctorId: String?, callback: (Doctor?) -> Unit) {
        db.collection("doctors").document(doctorId!!)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val doctor = document.toObject(Doctor::class.java)
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


    fun updateDoctorData(documentID: String, doctor: Doctor?) {
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

    /**
     * Uploads an image to Firebase Storage and updates the doctor's photo URL.
     * @param oldImageURL The URL of the old image to be replaced.
     * @param imageUri The URI of the new image to upload.
     * @param documentID The document ID of the doctor.
     * @param callback Callback function to handle the uploaded image URL.
     */
    fun uploadImageToFirebaseStorage(oldImageURL: String, imageUri: Uri, documentID: String, callback: (String?) -> Unit) {

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

    /**
     * Updates the doctor's photo URL in Firebase.
     * @param documentID The document ID of the doctor.
     * @param photoURL The URL of the new photo.
     */
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

    fun getPhotoForDoctor(documentID: String, callback: (String?) -> Unit) {
        db.collection("doctors")
            .document(documentID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val imageURL = document.toObject(Doctor::class.java)?.doctor_info?.photo
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

}