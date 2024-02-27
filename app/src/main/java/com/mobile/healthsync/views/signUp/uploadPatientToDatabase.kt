package com.mobile.healthsync.views.signUp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Patient

class uploadPatientToDatabase {
    fun uploadPatient(newPatient: Patient) {
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
}