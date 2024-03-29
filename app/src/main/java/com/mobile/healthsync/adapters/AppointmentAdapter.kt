package com.mobile.healthsync.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Appointment

class AppointmentAdapter(private var appointments: List<Appointment>, private var selectedDate: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_APPOINTMENT = 1
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    }

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientNameTextView: TextView = view.findViewById(R.id.patientNameTextView)
        val appointmentTimeTextView: TextView = view.findViewById(R.id.appointmentTimeTextView)
        val appointmentStartTimeTextView: TextView = view.findViewById(R.id.appointmentStartTimeTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_APPOINTMENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.header_layout, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_APPOINTMENT -> {
                val view = inflater.inflate(R.layout.item_appointment, parent, false)
                AppointmentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    //Last working Backup of BindViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            Log.d("AppointmentAdapter", "Binding header with date: $selectedDate")
            (holder as HeaderViewHolder).dateTextView.text = selectedDate
        } else {
            val appointment = appointments[position - 1] // Header is at position 0
            (holder as AppointmentViewHolder).patientNameTextView.text = appointment.patient_id.toString() // Assume there is a patientName in Appointment model
            holder.appointmentTimeTextView.text = getTimeSlotFromId(appointment.slot_id)
            holder.appointmentStartTimeTextView.text = getTimeSlotFromId(appointment.slot_id).substring(0,8)
        }
    }

    //Using Firebase to get Patient Name
   /* override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            Log.d("AppointmentAdapter", "Binding header with date: $selectedDate")
            (holder as HeaderViewHolder).dateTextView.text = selectedDate
        } else {
            val appointment = appointments[position - 1] // Header is at position 0
            val appointmentHolder = holder as AppointmentViewHolder
            appointmentHolder.appointmentTimeTextView.text = getTimeSlotFromId(appointment.slot_id)
            appointmentHolder.appointmentStartTimeTextView.text = getTimeSlotFromId(appointment.slot_id).substring(0, 8)

            // Fetch patient details from Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("Patients").document(appointment.patient_id.toString()).get()
                .addOnSuccessListener { documentSnapshot ->
                    val patient = documentSnapshot.toObject(Patient::class.java)
                    if (patient != null) {
                        // Access name and photo from patientDetails
                        val patientName = patient.patientDetails.name
                        val patientPhotoUrl = patient.patientDetails.photo

                        (holder as AppointmentViewHolder).patientNameTextView.text = patientName

                        // Load the photo URL into the ImageView using Glide or another image loading library
                        patientPhotoUrl?.let { url ->
                            Glide.with(holder.itemView.context)
                                .load(url)
                                .placeholder(R.drawable.placeholder) // Placeholder image
                                .error(R.drawable.error) // Error image
                                .into((holder as AppointmentViewHolder).profileImageView)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AppointmentAdapter", "Error fetching patient details", e)
                }
        }
    }*/


    override fun getItemCount(): Int {
        return appointments.size + 1 // +1 for the header
    }

    // Method to update the list of appointments and the selected date
    fun updateAppointments(newAppointments: List<Appointment>, newSelectedDate: String) {
        appointments = newAppointments
        selectedDate = newSelectedDate
        notifyDataSetChanged() // Notify any registered observers that the data set has changed.
    }

    private val slotTimes = mapOf(
        1 to "09:00 AM - 10:00 AM",
        2 to "10:00 AM - 11:00 AM",
        3 to "11:00 AM - 12:00 PM",
        4 to "12:00 PM - 01:00 PM",
        5 to "01:00 PM - 02:00 PM",
        6 to "02:00 PM - 03:00 PM",
        7 to "03:00 PM - 04:00 PM",
        8 to "04:00 PM - 05:00 PM",
        9 to "05:00 PM - 06:00 PM"

    )

    private fun getTimeSlotFromId(slotId: Int): String {
        return slotTimes.getOrElse(slotId) { "Unknown Time Slot" }
    }
}

