package com.mobile.healthsync.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Prescription

class PrescriptionRepository (private val context: Context) {

    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
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
                                medicineMap.schedule.morning.patientTook = intakeStatus
                                medicineMap.schedule.afternoon.patientTook = intakeStatus
                                medicineMap.schedule.night.patientTook = intakeStatus
                        //                                medicineMap.schedule.forEach{(_, scheduleMap) ->
                        //                                    if(scheduleMap.morning.doctorSaid){
                        //                                        scheduleMap.morning.patientTook = intakeStatus
                        //                                    }
                        //                                    if(scheduleMap.afternoon.doctorSaid){
                        //                                        scheduleMap.afternoon.patientTook = intakeStatus
                        //                                    }
                        //                                    if(scheduleMap.night.doctorSaid){
                        //                                        scheduleMap.night.patientTook = intakeStatus
                        //                                    }
                        //
                        //                                }
                            }
                            db.collection("prescriptions").document(document.id)
                                .set(prescription)
                                .addOnSuccessListener {
                                    showToast("Updated")
                                }
                                .addOnFailureListener { e ->
                                    showToast("Error updating prescription: ${e.message}")
                                }
                        }

                    } else {
                        showToast("Prescription not found")
                    }
                }
                else {
                    showToast("Error fetching patient data: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

//object PrescriptionRepository {
//
//    private val db = FirebaseFirestore.getInstance()
//
//    @JvmStatic
//    fun updateMedicinesForPrescription(prescriptionId: String, updatedMedicines: List<Medicine>) {
//        // Reference to the specific prescription document
//        val prescriptionRef = db.collection("prescriptions").document(prescriptionId)
//
//        // Update the 'medicines' field with the new data
//        prescriptionRef
//            .update("medicines", updatedMedicines)
//            .addOnSuccessListener {
//                // Handle success
//                Log.d("Medicines updated successfully for prescription", "$prescriptionId")
//            }
//            .addOnFailureListener { e ->
//                // Handle failures
//                Log.d("Error updating medicines for prescription", "$prescriptionId, $e")
//            }
//    }
//}

