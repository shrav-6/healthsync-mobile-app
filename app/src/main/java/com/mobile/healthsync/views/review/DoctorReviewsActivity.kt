package com.mobile.healthsync.views.review

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.ReviewsAdapter
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Reviews

/**
 * Activity to display reviews for a specific doctor.
 * Author: Zeel Ravalani
 */
class DoctorReviewsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_reviews)

        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getString("doctor_id", "-1")!!.toInt()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewsAdapter()
        recyclerView.adapter = adapter

        if (doctorId != -1) {
            fetchReviewsForDoctor(doctorId)
        } else {
            println("$doctorId does not exists")
        }
    }

    /**
     * Fetches reviews for the given doctorId and updates the RecyclerView with the reviews.
     * @param doctorId The ID of the doctor whose reviews are to be fetched.
     * Author: Zeel Ravalani
     */
    private fun fetchReviewsForDoctor(doctorId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("reviews")
            .whereEqualTo("doctor_id", doctorId)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Number of reviews: ${result.size()}")
                val reviewsList = mutableListOf<Reviews>()
                var reviewsProcessed = 0 // Counter to track processed reviews

                for (document in result) {
                    val review = document.toObject(Reviews::class.java)
                    // Fetch patient details for each review
                    db.collection("patients")
                        .whereEqualTo("patient_id", review.patientId) // Assuming patientId is the ID of the patient
                        .get()
                        .addOnSuccessListener { patientResult ->
                            Log.d(TAG, "Number of patient details fetched: ${patientResult.size()}")
                            Log.d(TAG, "Patient details fetched: ${patientResult}")
                            var patientName = "Anonymous" // Default name
                            for (patientDocument in patientResult) {
                                val patient = patientDocument.toObject(Patient::class.java)
                                // Update patientName for the current review
                                patientName = patient.patientDetails.name
                            }

                            // Add review with patient name to the list
                            reviewsList.add(review.copy(patientName = patientName))

                            // Increment counter
                            reviewsProcessed++

                            // Check if all reviews have been processed
                            if (reviewsProcessed == result.size()) {
                                // Update adapter with reviews list
                                adapter.setReviews(reviewsList)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors fetching patient details
                            Log.e(TAG, "Error fetching patient details", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e(TAG, "Error fetching reviews", exception)
            }
    }
}