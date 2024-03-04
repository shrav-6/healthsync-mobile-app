package com.mobile.healthsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

fun addRewardPointsToPatients() {
    val db = FirebaseFirestore.getInstance()
    val patientsCollection = db.collection("patients")

    patientsCollection.get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val patientId = document.id
                val data = hashMapOf("reward_points" to 30)

                // Update the document with the new field
                db.collection("patients").document(patientId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        println("Reward points added to patient $patientId successfully.")
                    }
                    .addOnFailureListener { e ->
                        println("Error adding reward points to patient $patientId: $e")
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error getting documents: $e")
        }
}
