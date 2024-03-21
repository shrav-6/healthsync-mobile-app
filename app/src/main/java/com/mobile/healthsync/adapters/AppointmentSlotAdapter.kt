package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Slot

class AppointmentSlotAdapter(val slotList: List<Slot>?) : RecyclerView.Adapter<AppointmentSlotAdapter.AppointmentSlotViewHolder>() {

    inner class AppointmentSlotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var slottext : TextView = itemView.findViewById(R.id.appointmentslot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentSlotViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.appointmentslot_layout,parent, false)
        return AppointmentSlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        return slotList?.size ?: 0
    }

    override fun onBindViewHolder(holder: AppointmentSlotViewHolder, position: Int) {
        holder.slottext.setText(slotList?.get(position)?.start_time + " - "+ slotList?.get(position)?.end_time)
    }

}