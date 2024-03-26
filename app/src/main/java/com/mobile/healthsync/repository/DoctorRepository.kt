package com.mobile.healthsync.repository

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Availability
import com.mobile.healthsync.model.Doctor


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
                    if (documents != null && !documents.isEmpty) {
                        // Documents found, parse data and add each Doctor object to the list
                        for (document in documents) {
                            val doctor = document.toObject(Doctor::class.java)
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
}