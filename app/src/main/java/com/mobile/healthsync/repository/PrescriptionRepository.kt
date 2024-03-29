package com.mobile.healthsync.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobile.healthsync.model.Medicine
import com.mobile.healthsync.model.Prescription

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
    fun loadPrescriptionsData(patientId: String) : Prescription {
        //val prescriptionId =
        //val prescriptionRef = db.collection("prescriptions").document(prescriptionId)

        val db = FirebaseFirestore.getInstance()
        lateinit var readPrescription: Prescription

        db.collection("appointments").whereEqualTo("patient_id",patientId.toInt())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Log.d("appointment documents", documents.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("error while reading from appointment table", exception.toString())
            }

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", "1").limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    Log.d("prescription read", document.data.toString())
                    readPrescription = document.toObject(Prescription::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                Log.w("prescription not found", "Error getting documents: ", exception)
            }

        return readPrescription
    }

    @JvmStatic
    fun loadMedicinesData(appointmentId: String) : List<Medicine> {
        // Reference to the "doctors" collection
        val db = Firebase.firestore
        Log.d("db", db.toString())
        Log.d("loadPrescriptionData", "Firestore instance obtained")
        lateinit var medicinesList: List<Medicine>

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", appointmentId).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    Log.d("document read", document.data.toString())
                    val prescription = document.toObject(Prescription::class.java)

                    val prescriptionID = prescription?.prescriptionId
                    Log.d("prescription",prescription.toString())

                    Log.d("prescriptionID", prescriptionID.toString())

                    // Assuming prescription is a valid Prescription object obtained from Firestore
                    val medicines: HashMap<String, Medicine>? = prescription?.medicines

                    //lateinit var medicinesList: List<Medicine>
                    Log.d("medicines", medicines.toString())
                    if (medicines != null && medicines.isNotEmpty()) {
                        medicinesList = medicines.values.toList()

                        Log.d("medicinesList", medicinesList.toString())
                    }
                    // Handle the retrieved prescription
                    //recyclerView.adapter = adapter
                } else {
                    // No prescription found with the given appointment ID
                    Log.d("Prescription read", "No prescription found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("document not found", "Error getting documents: ", exception)
            }
        return medicinesList
        //return arrayListOf(Medicine(name="crosine", dosage="4", numberOfDays=5, schedule=DaySchedule(morning=Schedule(doctorSaid=false, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=true), night=Schedule(doctorSaid=true, patientTook=false))), Medicine(name="dolo", dosage="2", numberOfDays=2, schedule= DaySchedule(morning=Schedule(doctorSaid=true, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=false), night= Schedule(doctorSaid=false, patientTook=false))))
    }
}
