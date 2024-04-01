package com.mobile.healthsync.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Patient

class SignupRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init{
        db = FirebaseFirestore.getInstance()
    }

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
                editor.putString("patient_id", newPatient.patient_id.toString())
                editor.putString("emailid",newPatient.email)
                editor.putString("password",newPatient.password)
                editor.apply()
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun createDoctor(newDoctor: Doctor, sharedPreferences: SharedPreferences) {
        val db = FirebaseFirestore.getInstance()
        val patientsCollection = db.collection("doctors")

        patientsCollection.add(newDoctor)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d("doctor signup done", "${documentReference.id}")

                val editor = sharedPreferences.edit()
                editor.putString("doctor_id", newDoctor.doctor_id.toString())
                editor.putString("emailid",newDoctor.email)
                editor.putString("password",newDoctor.password)
                editor.apply()
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
}