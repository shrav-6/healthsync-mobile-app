package com.mobile.healthsync.repository

import android.content.Context
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
        db.collection("prescriptions").whereEqualTo("prescription_id",prescriptionId)
            .get()
            .addOnCompleteListener { task : Task<QuerySnapshot> ->
                if(task.isSuccessful){
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            val prescription = document.toObject(Prescription::class.java)
                            prescription.medicines.forEach { (_, medicine) ->
                                medicine.schedule.forEach{(_, schedule) ->
                                    schedule.morning.patientTook = intakeStatus
                                    schedule.afternoon.patientTook = intakeStatus
                                    schedule.night.patientTook = intakeStatus
                                }
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