package com.mobile.healthsync.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor


class PatientAppointmentListAdapter(private var appointmentList: List<Appointment>, private val doctorList: List<Doctor>,
                                    private val onItemClick: (Appointment, Doctor) -> Unit) :
    RecyclerView.Adapter<PatientAppointmentListAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val doctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        val date: TextView = itemView.findViewById(R.id.tvDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val appointment = appointmentList[position]
                    val doctor_id = appointment.doctor_id
                    val doctor = doctorList.find { it.doctor_id == doctor_id }
                    Log.d("Doctor", " ${doctorList}")
//                    Log.d("Doctor", " id: ${doctor_id} ${doctor}")
                    if (doctor != null) {
                        onItemClick(appointment, doctor)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointmentList[position]
        val doctorId = appointment.doctor_id
        val doctor = doctorList.find { it.doctor_id == doctorId }
        Log.d("Doctor", "${doctor}")
        if (doctor != null) {
            holder.timeTextView.text = "${appointment.start_time} - ${appointment.end_time}"
            holder.date.text = "Date: ${appointment.date}"
            holder.doctorName.text = "Doctor: ${doctor.doctor_info.name}"
        } else {
            holder.timeTextView.text = "${appointment.start_time} - ${appointment.end_time}"
            holder.date.text = "Date: ${appointment.date}"
            holder.doctorName.text = "Doctor: Not found"
        }
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    fun updateList(newList: List<Appointment>) {
        appointmentList = newList
        notifyDataSetChanged()
    }

}