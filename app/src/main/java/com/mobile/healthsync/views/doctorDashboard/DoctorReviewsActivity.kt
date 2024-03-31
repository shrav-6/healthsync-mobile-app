package com.mobile.healthsync.views.doctorDashboard

import ReviewsAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Reviews
import android.content.Context
import android.util.Log
import com.mobile.healthsync.BaseActivityForDoctor

/**
 * DoctorReviewsActivity displays reviews for a specific doctor.
 * @constructor Creates an instance of DoctorReviewsActivity.
 * @author Zeel Ravalani
 */
class DoctorReviewsActivity : BaseActivityForDoctor() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_reviews)

        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getString("doctor_id", "-1")!!.toInt()

        if (doctorId != -1) {
            fetchReviewsForDoctor(doctorId)
        } else {
            println("$doctorId does not exist")
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewsAdapter()
        recyclerView.adapter = adapter

    }

    /**
     * Fetches reviews for the given doctor ID.
     * @param doctorId The ID of the doctor for whom reviews are to be fetched.
     * @author Zeel Ravalani
     */
    private fun fetchReviewsForDoctor(doctorId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("reviews")
            .whereEqualTo("doctor_id", doctorId)
            .get()
            .addOnSuccessListener { result ->
                val reviewsList = mutableListOf<Reviews>()
                for (document in result) {
                    val review = document.toObject(Reviews::class.java)
                    reviewsList.add(review)
                }
                adapter.setReviews(reviewsList)
            }
            .addOnFailureListener { exception ->
                // Handle errors fetching reviews
                Log.e(TAG, "Error fetching reviews", exception)
            }
    }
}
