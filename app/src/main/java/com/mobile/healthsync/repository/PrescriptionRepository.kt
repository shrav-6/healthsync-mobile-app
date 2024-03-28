package com.mobile.healthsync.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Medicine

object PrescriptionRepository {

    private val db = FirebaseFirestore.getInstance()

    @JvmStatic
    fun updateMedicinesForPrescription(prescriptionId: String, updatedMedicines: List<Medicine>) {
        // Reference to the specific prescription document
        val prescriptionRef = db.collection("prescriptions").document(prescriptionId)

        // Update the 'medicines' field with the new data
        prescriptionRef
            .update("medicines", updatedMedicines)
            .addOnSuccessListener {
                // Handle success
                Log.d("Medicines updated successfully for prescription", "$prescriptionId")
            }
            .addOnFailureListener { e ->
                // Handle failures
                Log.d("Error updating medicines for prescription", "$prescriptionId, $e")
            }
    }
}
