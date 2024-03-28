package com.mobile.healthsync.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor

class AppointmentDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get appointment and doctor data from arguments
        val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)
        val doctor: Doctor? = arguments?.getParcelable(DOCTOR_KEY)

        // Update UI with appointment and doctor data
        if (appointment != null && doctor != null) {
            view.findViewById<TextView>(R.id.textDate).text = "Date: ${appointment.date}"
            view.findViewById<TextView>(R.id.textTime).text = "Time: ${appointment.start_time} - ${appointment.end_time}"
            view.findViewById<TextView>(R.id.textDoctorName).text = "Doctor: ${doctor.doctor_info.name}"
            view.findViewById<TextView>(R.id.textSpecialty).text = "Specialty: ${doctor.doctor_speciality}"
            // Add more setText() calls for other appointment and doctor details TextViews as needed
        }
    }

    companion object {
        private const val APPOINTMENT_KEY = "appointment"
        private const val DOCTOR_KEY = "doctor"

        fun newInstance(appointment: Appointment, doctor: Doctor): AppointmentDetailsFragment {
            val fragment = AppointmentDetailsFragment()
            val args = Bundle()
            args.putParcelable(APPOINTMENT_KEY, appointment)
            args.putParcelable(DOCTOR_KEY, doctor)
            fragment.arguments = args
            return fragment
        }
    }
}
