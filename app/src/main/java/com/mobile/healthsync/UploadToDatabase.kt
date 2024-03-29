package com.mobile.healthsync

import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Doctor
import io.grpc.Context

class uploadToDatabase {
    //lateinit var sharedPreferences: SharedPreferences

    fun createPatient(newPatient: Patient, sharedPreferences: SharedPreferences)  {
        val db = FirebaseFirestore.getInstance()
        val patientsCollection = db.collection("patients")
        //lateinit var sharedPreferences: SharedPreferences

        patientsCollection.add(newPatient)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d("patient signup done", "${documentReference.id}")

                // add to sharedPreferences, TODO: in signin not signup - this is just for testing
                val editor = sharedPreferences.edit()
                editor.putString("PatientID", "${documentReference.id}")
                editor.apply()
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


