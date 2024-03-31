package com.mobile.healthsync.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Prescription.Medicine


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

    fun updatePatientMedicineIntake(prescriptionId: Int, intakeStatus: Boolean) {
        Log.d("prescription_id", prescriptionId.toString())
        db.collection("prescriptions").whereEqualTo("prescription_id",prescriptionId)
            .get()
            .addOnCompleteListener { task : Task<QuerySnapshot> ->
                if(task.isSuccessful){
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            val prescription = document.toObject(Prescription::class.java)
                            prescription.medicines?.forEach { (_, medicineMap) ->
                                if(medicineMap.schedule.morning.doctorSaid){
                                    medicineMap.schedule.morning.patientTook = intakeStatus
                                }
                                if(medicineMap.schedule.afternoon.doctorSaid){
                                    medicineMap.schedule.afternoon.patientTook = intakeStatus
                                }
                                if(medicineMap.schedule.night.doctorSaid){
                                    medicineMap.schedule.night.patientTook = intakeStatus
                                }
                            }
                            db.collection("prescriptions").document(document.id)
                                .set(prescription)
                                .addOnSuccessListener {
                                    Log.d("Updated", prescriptionId.toString())
                                }
                                .addOnFailureListener { e ->
                                    Log.d("Error updating prescription:", e.message.toString())
                                }
                        }

                    } else {
                        Log.d("status","Prescription not found")
                    }
                }
                else {
                    Log.d("status","Error fetching patient data: ${task.exception?.message}")
                }
            }
    }

}

