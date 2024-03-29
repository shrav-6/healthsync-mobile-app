package com.mobile.healthsync.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Ratings
import javax.security.auth.callback.Callback

class ReviewRepository {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getReviews(doctor_id : Int, callback: (MutableList<Ratings>?) -> Unit)
    {
        var reviews = mutableListOf<Ratings>()
        db.collection("reviews")
            .whereEqualTo("doctor_id",doctor_id)
            .get()
            .addOnCompleteListener { task : Task<QuerySnapshot> ->
                if(task.isSuccessful){
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for(document in documents) {
                            val review = document.toObject(Ratings::class.java)
                            reviews.add(review)
                        }
                        callback(reviews)
                    }
                }
            }
    }
}