package com.mobile.healthsync.views.patientDashboard

import android.util.Log
import com.mobile.healthsync.model.Appointment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R

class AppointmentAdapter(private var appointments: List<Appointment>) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientNameTextView: TextView = view.findViewById(R.id.patientNameTextView)
        val appointmentTimeTextView: TextView = view.findViewById(R.id.appointmentTimeTextView)
        // Other views for the appointment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.patientNameTextView.text = appointment.patient_id.toString()
        //holder.appointmentTimeTextView.text = appointment.start_time + " - " + appointment.end_time
        holder.appointmentTimeTextView.text = getTimeSlotFromId(appointment.slot_id)
        // Bind other data to the views
    }

    override fun getItemCount(): Int = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        Log.d("AppointmentAdapter", "Updating appointments: ${newAppointments.size}")
        appointments = newAppointments
        notifyDataSetChanged()
    }


    val slotTimes = mapOf(
        1 to "9:00 AM - 10:00 AM",
        2 to "10:00 AM - 11:00 AM",
        3 to "11:00 AM - 12:00 PM",
        4 to "12:00 PM - 1:00 PM",
        5 to "1:00 PM - 2:00 PM",
        6 to "2:00 PM - 3:00 PM",
        7 to "3:00 PM - 4:00 PM",
        8 to "4:00 PM - 5:00 PM",
        9 to "5:00 PM - 6:00 PM"
    )

    fun getTimeSlotFromId(slotId: Int): String {
        return slotTimes.getOrElse(slotId) { "Unknown Time Slot" }
    }

}
