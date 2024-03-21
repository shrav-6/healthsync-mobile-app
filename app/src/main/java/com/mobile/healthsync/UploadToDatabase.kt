package com.mobile.healthsync

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Doctor

class uploadToDatabase {
    fun createPatient(newPatient: Patient) {
        val db = FirebaseFirestore.getInstance()
        val patientsCollection = db.collection("patients")

        patientsCollection.add(newPatient)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d("patient signup done", "${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun createDoctor(newDoctor: Doctor) {
        val db = FirebaseFirestore.getInstance()
        val patientsCollection = db.collection("doctors")

        patientsCollection.add(newDoctor)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d("doctor signup done", "${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
}