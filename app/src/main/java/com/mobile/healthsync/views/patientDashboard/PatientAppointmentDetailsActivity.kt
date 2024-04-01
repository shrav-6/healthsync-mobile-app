package com.mobile.healthsync.views.patientDashboard

import android.os.Bundle
import android.widget.Button
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.fragments.AppointmentDetailsFragment
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor

class PatientAppointmentDetailsActivity : BaseActivity() {

    private lateinit var downloadButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_details)



        // Retrieve appointment and doctor data from intent
        val appointment: Appointment? = intent.getParcelableExtra(APPOINTMENT_KEY)
        val doctor: Doctor? = intent.getParcelableExtra(DOCTOR_KEY)

        // Display the fragment
        if (appointment != null && doctor != null) {
            val appointmentDetailsFragment = AppointmentDetailsFragment.newInstance(
                appointment,
                doctor
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, appointmentDetailsFragment)
                .commit()
        }
    }

    companion object {
        const val APPOINTMENT_KEY = "appointment"
        const val DOCTOR_KEY = "doctor"
    }
}