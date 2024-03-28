package com.mobile.healthsync.repository

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Appointment

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

    fun createAppointment(doctor_id: Int, patient_id: Int, slot_id: Int, date: String,start_time : String, callback: () -> Unit){

        val appointment = Appointment(-1,doctor_id, patient_id,date,slot_id,start_time)
        db.collection("appointments")
            .add(appointment)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                showToast("Booking Complete")
                callback()
            }
            .addOnFailureListener { e ->
                println("DocumentSnapshot addeition failed")
                showToast("Booking Incomplete due to Technical errors")
            }
    }

    fun fixAppointment(doctor_id: Int, datestr: String, slot_id: Int, payment_id: Int) {
        db.collection("appointments")
            .whereEqualTo("doctor_id", doctor_id)
            .whereEqualTo("date", datestr)
            .whereEqualTo("slot_id",slot_id).get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.elementAt(0)
                        val appointment = document.toObject(Appointment::class.java)
                        appointment.appointment_status = true
                        appointment.payment_id = payment_id
                        db.collection("appointments").document(document.id)
                            .set(appointment)
                            .addOnSuccessListener {
                                println("Appointment updated successfully")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating appointment: ${e.localizedMessage}")
                                showToast("Technical Errors during Payment Update: ${task.exception?.message}")
                            }
                    }
                }
                else {
                    showToast("Technical Errors during Payment Update: ${task.exception?.message}")
                }
            }
    }
}