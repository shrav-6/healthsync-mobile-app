package com.mobile.healthsync.repository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.model.Event

class EventsRepository(private val context: Context) {

    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getEventsBySpecificField(fieldName: String, fieldValue: String, callback: (List<Event>) -> Unit) {
        db.collection("events")
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val eventsList = mutableListOf<Event>()
                    for (document in task.result!!) {
                        val event = document.toObject(Event::class.java)
                        eventsList.add(event)
                    }
                    callback(eventsList)
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching event data: $exceptionMessage")
                    callback(emptyList())
                }
            }
    }

    fun getAllEvents(callback: (List<Event>) -> Unit) {
        db.collection("events")
            .get()
            .addOnCompleteListener { task ->
                task
                if (task.isSuccessful) {
                    val eventList = task.result?.toObjects(Event::class.java) ?: emptyList()
                    callback(eventList)
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    showToast("Error fetching events: $exceptionMessage")
                    callback(emptyList())
                }
            }
    }

    private fun showToast(message: String) {
        // Show a toast message (you can replace this with your preferred error handling mechanism)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}