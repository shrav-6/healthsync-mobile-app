package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Reviews

/**
 * Adapter for displaying reviews in a RecyclerView.
 * Author: Zeel Ravalani
 */
class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    private var reviewsList: List<Reviews> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentItem = reviewsList[position]
        holder.textPatientName.text = currentItem.patientName // Set patient name
        holder.textComment.text = currentItem.comment
        holder.ratingBar.rating = currentItem.stars.toFloat()
    }

    override fun getItemCount() = reviewsList.size

    /**
     * Sets the list of reviews and notifies the adapter of the data set change.
     * @param reviews The list of reviews to be displayed.
     * Author: Zeel Ravalani
     */
    fun setReviews(reviews: List<Reviews>) {
        this.reviewsList = reviews
        notifyDataSetChanged()
    }

    /**
     * ViewHolder for the review item in the RecyclerView.
     * Author: Zeel Ravalani
     */
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textPatientName: TextView = itemView.findViewById(R.id.textPatientName)
        val textComment: TextView = itemView.findViewById(R.id.textComment)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}


