package com.mobile.healthsync.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Event
import com.mobile.healthsync.views.events.EventInfoActivity
import com.mobile.healthsync.views.events.EventViewHolder

/**
 * Adapter for populating RecyclerView with Event items.
 *
 * @param events List of events to be displayed.
 */
class EventTypeAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventViewHolder>() {

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_event_view_holder, parent, false)
        return EventViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = events[position]

        if (currentEvent.status == "cancelled" || currentEvent.status == "closed" ) {
            holder.itemView.isEnabled = false
            holder.info.text = "Not Available \uD83D\uDEAB"
        }
        else {
            holder.info.text = "👆 Click for more information"
        }

        // Setting the values to display on the Recycle Item View
        holder.eventTitle.text = currentEvent.eventName
        holder.eventDate.text = currentEvent.datePublished

        when (currentEvent.type) {
            "donation" -> holder.image.setImageResource(R.drawable.donation)
            "fundraiser" -> holder.image.setImageResource(R.drawable.fund)
            "info session" -> holder.image.setImageResource(R.drawable.info)
            "volunteer" -> holder.image.setImageResource(R.drawable.volunteer)
        }

        // When clicking the itemView, it passes the values of the City Model as arguments to CityInfoActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EventInfoActivity::class.java)
            intent.putExtra("currentEvent", currentEvent)
            holder.itemView.context.startActivity(intent)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return events.size
    }
}
