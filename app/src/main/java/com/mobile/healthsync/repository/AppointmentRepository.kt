package com.mobile.healthsync.repository

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Appointment
import java.util.Random

class AppointmentRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    private fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun generateUniquePaymentId(): Int {
        val timestampPart = (System.currentTimeMillis() % 100000).toInt() // Last 5 digits of the current timestamp
        val randomPart = Random().nextInt(900) + 100 // Ensures a 3-digit random number
        return timestampPart * 1000 + randomPart // Combines both parts
    }

    fun getAppointments(doctor_id: Int, datestr: String,callback: (MutableList<Appointment>) -> Unit)
    {
        val appointmentlist = mutableListOf<Appointment>()
        db.collection("appointments")
            .whereEqualTo("doctor_id", doctor_id)
            .whereEqualTo("date", datestr).get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        // Documents found, parse data and add each appointment object to the list
                        for (document in documents) {
                            val appointment = document.toObject(Appointment::class.java)
                            appointment?.let { appointmentlist.add(it) }
                        }
                    }
                } else {
                    showToast("Error fetching doctors: ${task.exception?.message}")
                }
                callback(appointmentlist)
            }
    }

    fun getAppointments(patientId: Int, callback: (MutableList<Appointment>) -> Unit) {
        val appointmentList = mutableListOf<Appointment>()
        db.collection("appointments")
            .whereEqualTo("patient_id", patientId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        // Documents found, parse data and add each appointment object to the list
                        for (document in documents) {
                            val appointment = document.toObject(Appointment::class.java)
                            appointment?.let { appointmentList.add(it) }
                        }
                    }
                } else {
                    // Error handling
                    showToast("Error fetching appointments: ${task.exception?.message}")
                }
                // Callback with the list of appointments
                callback(appointmentList)
            }
    }

    
    fun createAppointment(doctor_id: Int, patient_id: Int, slot_id: Int, date: String,start_time : String, callback: (Int) -> Unit){

        val appointment_id = generateUniquePaymentId()
        val appointment = Appointment(appointment_id,doctor_id, patient_id,date,slot_id,start_time)
        db.collection("appointments")
            .add(appointment)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                showToast("Booking Complete")
                callback(appointment_id)
            }
            .addOnFailureListener { e ->
                println("DocumentSnapshot addeition failed")
                showToast("Booking Incomplete due to Technical errors")
            }
    }
}