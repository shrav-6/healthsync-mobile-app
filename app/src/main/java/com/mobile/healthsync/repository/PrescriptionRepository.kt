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

//    private fun getUserFirebaseToken(): String {
//        var token = "";
//        FirebaseMessaging.getInstance().getToken()
//            .addOnCompleteListener { task: Task<String> ->
//                if (task.isSuccessful) {
//                    token = task.result
//                    Log.d("firebase token", "Firebase Token: $token")
//                    // Handle the token (e.g., send it to your server)
//                } else {
//                    Log.e("firebase token", "Failed to get Firebase token: " + task.exception)
//                }
//            }
//        return token;
//    }
//
//    private fun getPatientIdFromToken(token: String): Int {
//        var patientId = -1;
//        // Reference to the "patients" collection
//        db.collection("patients").whereEqualTo("token",token)
//            .get()
//            .addOnCompleteListener { task: Task<QuerySnapshot> ->
//                if (task.isSuccessful) {
//                    val documents = task.result
//                    if (documents != null && !documents.isEmpty) {
//                        for (document in documents) {
//                            val patient = document.toObject(Patient::class.java)
//                            patientId = patient.patient_id
//                        }
//                    } else {
//                        showToast("Patient not found")
//                    }
//                } else {
//                    showToast("Error fetching patient data: ${task.exception?.message}")
//                }
//            }
//        return patientId
//    }
//
//    private fun getAllAppointmentIdsFromPatientId(patientId: Int): MutableList<Int> {
//        val appointmentId = mutableListOf<Int>()
//        if(patientId == -1){
//            return appointmentId
//        }
//        // Reference to the "patients" collection
//        db.collection("appointments").whereEqualTo("patient_id", patientId)
//            .get()
//            .addOnCompleteListener { task: Task<QuerySnapshot> ->
//                if (task.isSuccessful) {
//                    val documents = task.result
//                    if (documents != null && !documents.isEmpty) {
//                        for (document in documents) {
//                            val appointment = document.toObject(Appointment::class.java)
//                            appointmentId.add(appointment.appointment_id)
//                        }
//                    } else {
//                        showToast("Patient not found")
//                    }
//                } else {
//                    showToast("Error fetching patient data: ${task.exception?.message}")
//                }
//            }
//        return appointmentId
//    }
//
//    private fun getPrescriptionIdFromAppointmentId(appointmentId: Int): Int {
//        var prescriptionId = -1;
//        // Reference to the "patients" collection
//        db.collection("prescriptions").whereEqualTo("appointment_id",appointmentId)
//            .get()
//            .addOnCompleteListener { task: Task<QuerySnapshot> ->
//                if (task.isSuccessful) {
//                    val documents = task.result
//                    if (documents != null && !documents.isEmpty) {
//                        for (document in documents) {
//                            val prescription = document.toObject(Prescription::class.java)
//                            prescriptionId = prescription.prescriptionId.toInt()
//                        }
//                    } else {
//                        showToast("Patient not found")
//                    }
//                } else {
//                    showToast("Error fetching patient data: ${task.exception?.message}")
//                }
//            }
//        return prescriptionId
//    }
//
//
//    fun getPrescriptionIdFromToken(token: String): MutableList<Int> {
//
//        val patientId = getPatientIdFromToken(token)
//        val appointmentId = getAllAppointmentIdsFromPatientId(patientId)
//        val prescriptionId = mutableListOf<Int>()
//
//        for (id in appointmentId){
//            prescriptionId.add(getPrescriptionIdFromAppointmentId(id))
//        }
//
//        return prescriptionId
//
//    }

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