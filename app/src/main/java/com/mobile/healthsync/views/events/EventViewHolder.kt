package com.mobile.healthsync.views.events

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R

/**
 * ViewHolder class for holding views of an event item in a RecyclerView.
 * @param itemView The view of the event item.
 */
class EventViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    // Initialize views from event item layout
    val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
    val image: ImageView = itemView.findViewById(R.id.card_image)
    val eventDate: TextView = itemView.findViewById(R.id.eventDate)
    val info: TextView = itemView.findViewById(R.id.info)
}