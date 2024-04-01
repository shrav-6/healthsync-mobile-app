package com.mobile.healthsync.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mobile.healthsync.model.Event
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Prescription
import java.util.UUID


class InsightsRepository(private val context: Context) {

    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getappointmentId(patient_id: Int?, callback: (Int?) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("patient_id", patient_id)
            //.orderBy("date", Query.Direction.DESCENDING) //not working
            .limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val appointmentId = document.getLong("appointment_id")!!.toInt()
                        callback(appointmentId)
                    } else {
                        callback(null) // No appointment found
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    Log.d("Error fetching appointment id:", exceptionMessage)
                    callback(null) // Error occurred
                }
            }
    }

    fun getPrescriptionForInsights(
        patient_id: Int?,
        key: String = "appointment_id",
        callback: (Prescription) -> Unit
    ) {

        getappointmentId(patient_id) { appointmentId ->
            val apid = appointmentId!!
            Log.d("apid",apid.toString())
            db.collection("prescriptions")
                .whereEqualTo(key, apid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot != null) {
                            for (document in querySnapshot.documents) {
                                val readPrescription = document.toObject(Prescription::class.java)!!
                                callback(readPrescription)
                                return@addOnCompleteListener // Exit the loop after the first document
                            }
                        }
                    } else {
                        val exceptionMessage = task.exception?.message ?: "Unknown error"
                        showToast("Error fetching prescription data: $exceptionMessage")
                        callback(Prescription())
                    }
                    // If no documents found or an error occurred, invoke the callback with an empty Prescription
                    callback(Prescription())
                }
        }
    }


    public fun upload() {
        val prescription = Prescription(
            appointmentId = 3,
            prescriptionId = 3,
            medicines = hashMapOf(
                "medicine1" to Prescription.Medicine(
                    name = "Medicine A",
                    dosage = "10mg",
                    numberOfDays = 7,
                    schedule = Prescription.Medicine.DaySchedule(
                        morning = Prescription.Medicine.DaySchedule.Schedule(
                            doctorSaid = true,
                            patientTook = false
                        ),
                        afternoon = Prescription.Medicine.DaySchedule.Schedule(
                            doctorSaid = true,
                            patientTook = true
                        ),
                        night = Prescription.Medicine.DaySchedule.Schedule(
                            doctorSaid = true,
                            patientTook = true
                        )
                    )
                ),
                "medicine2" to Prescription.Medicine(
                    name = "Medicine B",
                    dosage = "5mg",
                    numberOfDays = 10,
                    schedule = Prescription.Medicine.DaySchedule(
                        morning = Prescription.Medicine.DaySchedule.Schedule(
                            doctorSaid = true,
                            patientTook = false
                        ),
                        afternoon = Prescription.Medicine.DaySchedule.Schedule(
                            doctorSaid = true,
                            patientTook = true
                        ),
                        night = Prescription.Medicine.DaySchedule.Schedule(
                                doctorSaid = true,
                            patientTook = true
                        )
                    )
                )
            )
        )

        db.collection("prescriptions").add(prescription)
            .addOnSuccessListener { documentReference ->
                Log.d("DocumentSnapshot added with ID:","${documentReference.id}")


            }
            .addOnFailureListener { e ->
                Log.d("Error adding document:","$e")
            }

    }

    fun getAppointmentDetails(patientId: Int?, callback: (List<String>?) -> Unit) {
        val appointmentDetails = mutableListOf<String>()

        // First, query the appointments collection based on patient ID
        db.collection("appointments")
            .whereEqualTo("patient_id", patientId)
            .limit(1)
            .get()
            .addOnCompleteListener { appointmentTask ->
                if (appointmentTask.isSuccessful) {
                    val querySnapshot = appointmentTask.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val appointmentId = document.getLong("appointment_id")!!.toInt()
                        Log.d("getAppointmentDetails", appointmentId.toString())

                        // Now that we have the appointment ID, call getAppointmentDetailsInner
                        Log.d("before calling innner", appointmentId.toString())
                        getAppointmentDetailsInner(appointmentId, callback)
                    } else {
                        callback(null) // No appointment found
                    }
                } else {
                    val exceptionMessage = appointmentTask.exception?.message ?: "Unknown error"
                    Log.d("Error fetching appointment id:", exceptionMessage)
                    callback(null) // Error occurred
                }
            }
    }

    // Function to fetch appointment details based on appointment ID
    private fun getAppointmentDetailsInner(appointmentId: Int, callback: (List<String>?) -> Unit) {
        val appointmentDetails = mutableListOf<String>()

        // Query appointments collection based on appointment ID
        db.collection("appointments")
            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnCompleteListener { appointmentTask ->
                if (appointmentTask.isSuccessful) {
                    val appointmentDocument = appointmentTask.result?.documents?.firstOrNull()
                    if (appointmentDocument != null) {
                        Log.d("received appointmentdoc",appointmentDocument.toString())
                        val appointmentDate = appointmentDocument.getString("date")
                        val doctorId = appointmentDocument.getLong("doctor_id")!!.toInt()

                        if (appointmentDate != null && doctorId != null) {
                            // Query doctors collection based on doctor ID
                            db.collection("doctors")
                                .whereEqualTo("doctor_id", doctorId)
                                .get()
                                .addOnCompleteListener { doctorTask ->
                                    if (doctorTask.isSuccessful) {
                                        val doctorDocument = doctorTask.result?.documents?.firstOrNull()
                                        if (doctorDocument != null) {
                                            val doctorInfo = doctorDocument.get("doctor_info") as? Map<String, Any>
                                            val doctorName = doctorInfo?.get("name") as? String
                                            if (doctorName != null) {
                                                appointmentDetails.add(appointmentDate)
                                                appointmentDetails.add(doctorName)
                                                callback(appointmentDetails)
                                            } else {
                                                callback(null) // Doctor name not found
                                            }
                                        } else {
                                            callback(null) // Doctor document not found
                                        }
                                    } else {
                                        val exceptionMessage = doctorTask.exception?.message ?: "Unknown error"
                                        Log.d("Error fetching doctor details:", exceptionMessage)
                                        callback(null) // Error occurred while fetching doctor details
                                    }
                                }
                        } else {
                            callback(null) // Appointment date or doctor ID not found
                        }
                    } else {
                        callback(null) // Appointment document not found
                    }
                } else {
                    val exceptionMessage = appointmentTask.exception?.message ?: "Unknown error"
                    Log.d("Error fetching appointment details:", exceptionMessage)
                    callback(null) // Error occurred while fetching appointment details
                }
            }
    }


    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}