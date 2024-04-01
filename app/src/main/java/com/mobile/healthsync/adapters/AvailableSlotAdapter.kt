package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Slot

/**
 * Adapter class for displaying available slots in a RecyclerView.
 *
 * @param slotList List of available slots to be displayed.
 */
class AvailableSlotAdapter(val slotList: List<Slot>?) : RecyclerView.Adapter<AvailableSlotAdapter.AppointmentSlotViewHolder>() {

    /**
     * View holder class for individual appointment slots.
     *
     * @param itemView The view for an individual appointment slot item.
     */
    inner class AppointmentSlotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        // TextView for displaying slot details
        var slottext : TextView = itemView.findViewById(R.id.appointmentslot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentSlotViewHolder {
        // Inflate the layout for an individual appointment slot item
        val v = LayoutInflater.from(parent.context).inflate(R.layout.appointmentslot_layout,parent, false)
        return AppointmentSlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        // Return the total number of available slots, or 0 if slotList is null
        return slotList?.size ?: 0
    }

    override fun onBindViewHolder(holder: AppointmentSlotViewHolder, position: Int) {
        // Bind slot data to the view holder
        holder.slottext.setText(slotList?.get(position)?.start_time + " - "+ slotList?.get(position)?.end_time)
    }

}