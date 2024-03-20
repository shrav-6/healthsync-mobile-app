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
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getAppointments(doctor_id: Int, datestr: String,callback: (MutableList<Appointment>) -> Unit)
    {
        val appointmentlist = mutableListOf<Appointment>()
        val task = db.collection("appointments")
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
}