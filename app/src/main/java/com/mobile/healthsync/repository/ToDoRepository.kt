package com.mobile.healthsync.repository

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobile.healthsync.model.Prescription

class ToDoRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getappointmentAndPrescriptionId(
        patient_id: Int?,
        callback: (List<String>?) -> Unit
    ) {
        db.collection("appointments")
            .whereEqualTo("patient_id", patient_id)
            .limit(1)
            .get()
            .addOnCompleteListener { appointmentTask ->
                if (appointmentTask.isSuccessful) {
                    val querySnapshot = appointmentTask.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val appointmentId = document.getLong("appointment_id")!!.toInt()

                        Log.d("in todo: appointmentid", appointmentId.toString())

                        db.collection("prescriptions")
                            .whereEqualTo("appointment_id", appointmentId)
                            .limit(1)
                            .get()
                            .addOnCompleteListener { prescriptionTask ->
                                if (prescriptionTask.isSuccessful) {
                                    val prescriptionQuerySnapshot = prescriptionTask.result
                                    if (prescriptionQuerySnapshot != null && !prescriptionQuerySnapshot.isEmpty) {
                                        val prescriptionDocument =
                                            prescriptionQuerySnapshot.documents[0]
                                        val prescriptionId =
                                            prescriptionDocument.getLong("prescription_id")!!
                                                .toInt()

                                        val documentId = prescriptionDocument.id
                                        val mylist = listOf(documentId, appointmentId.toString(), prescriptionId.toString())

                                        Log.d("in todo: prescription_id", prescriptionId.toString())
                                        // Callback with appointment_id and prescription_id
                                        callback(mylist)
                                    } else {
                                        callback(null) // No prescription found
                                    }
                                } else {
                                    val exceptionMessage =
                                        prescriptionTask.exception?.message ?: "Unknown error"
                                    Log.d("Error fetching prescription id:", exceptionMessage)
                                    callback(null) // Error occurred while fetching prescription id
                                }
                            }
                    } else {
                        callback(null) // No appointment found
                    }
                } else {
                    val exceptionMessage = appointmentTask.exception?.message ?: "Unknown error"
                    Log.d("Error fetching appointment id:", exceptionMessage)
                    callback(null) // Error occurred while fetching appointment id
                }
            }
    }


    fun updateMedicinesForPrescription(
        documentId: String,
        appointmentId: Int,
        prescriptionId: Int,
        updatedMedicines: List<Prescription.Medicine>
    ) {
            Log.d("document id received in update func",documentId)
            Log.d("Appointment ID in update func:", appointmentId.toString())
            Log.d("Prescription ID in update func:", prescriptionId.toString())

            Log.d("update todo in db", "$updatedMedicines\n$prescriptionId")
            var hashMap = HashMap<String, Prescription.Medicine>()

            for ((index, medicine) in updatedMedicines.withIndex()) {
                val key = "medicine_${updatedMedicines.size - index}"
                hashMap[key] = medicine
            }

            val updatedPrescription = Prescription(
                appointmentId = appointmentId,
                prescriptionId = prescriptionId,
                medicines = hashMap
            )

            Log.d("docid, prescription","$documentId\n $updatedPrescription")
            db.collection("prescriptions").document(documentId)
                .set(updatedPrescription)
                .addOnCompleteListener {
                    Log.d("updated to do in db","updated")
                }
            }

    fun loadPrescriptionsData(patientId: String): Prescription {
        //val prescriptionId =
        //val prescriptionRef = db.collection("prescriptions").document(prescriptionId)

        val db = FirebaseFirestore.getInstance()
        lateinit var readPrescription: Prescription

        db.collection("appointments")
            .whereEqualTo("patient_id", patientId.toInt())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Log.d("appointment documents", documents.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w(
                    "error while reading from appointment table",
                    exception.toString()
                )
            }

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", "3").limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    Log.d("prescription read", document.data.toString())
                    readPrescription =
                        document.toObject(Prescription::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                Log.w(
                    "prescription not found",
                    "Error getting documents: ",
                    exception
                )
            }

        return readPrescription
    }

    fun loadMedicinesData(
        appointmentId: Int,
        callback: (List<Prescription.Medicine>?) -> Unit
    ) {
        // Reference to the "doctors" collection
        val db = Firebase.firestore
        Log.d("db", db.toString())
        Log.d("loadPrescriptionData", "Firestore instance obtained")
        //var medicinesList: List<Prescription.Medicine> = emptyList()

        Log.d("app in todo repo", appointmentId.toString())

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", appointmentId).limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null) {
                        for (document in querySnapshot.documents) {
                            val prescription =
                                document.toObject(Prescription::class.java)!!
                            val prescriptionID = prescription?.prescriptionId
                            Log.d("prescription", prescription.toString())

                            Log.d("prescriptionID", prescriptionID.toString())

                            // Assuming prescription is a valid Prescription object obtained from Firestore
                            val medicines: HashMap<String, Prescription.Medicine>? =
                                prescription?.medicines

                            //lateinit var medicinesList: List<Medicine>
                            Log.d("medicines", medicines.toString())
                            if (medicines != null && medicines.isNotEmpty()) {
                                val medicinesList = medicines.values.toList()

                                Log.d("medicinesList", medicinesList.toString())
                                callback(medicinesList)
                            }
                        }
                    }
                } else {
                    callback(null)
                }
            }
    }
}