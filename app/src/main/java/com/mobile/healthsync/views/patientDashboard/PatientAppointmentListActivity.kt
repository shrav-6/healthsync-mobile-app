package com.mobile.healthsync.views.patientDashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.PatientAppointmentListAdapter
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.repository.AppointmentRepository
import com.mobile.healthsync.repository.DoctorRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientAppointmentListActivity : BaseActivity() {


    //    listOf(
//        Appointment(
//            appointment_id = 1,
//            doctor_id = 1,
//            patient_id = 1,
//            date = "03/24/2024",
//            start_time = "3:00 PM",
//            end_time = "4:00 PM",
//            payment_id = 1,
//            appointment_status = false
//        ),
//        Appointment(
//            appointment_id = 2,
//            doctor_id = 2,
//            patient_id = 2,
//            date = "03/25/2024",
//            start_time = "2:00 PM",
//            end_time = "3:00 PM",
//            payment_id = 2,
//            appointment_status = true
//        ),
//        Appointment(
//            appointment_id = 3,
//            doctor_id = 3,
//            patient_id = 3,
//            date = "03/25/2024",
//            start_time = "1:00 PM",
//            end_time = "2:00 PM",
//            payment_id = 3,
//            appointment_status = false
//        ),
//        Appointment(
//            appointment_id = 4,
//            doctor_id = 4,
//            patient_id = 4,
//            date = "03/27/2024",
//            start_time = "12:00 PM",
//            end_time = "1:00 PM",
//            payment_id = 4,
//            appointment_status = true
//        ),
//        Appointment(
//            appointment_id = 5,
//            doctor_id = 5,
//            patient_id = 5,
//            date = "03/27/2024",
//            start_time = "11:00 AM",
//            end_time = "12:00 PM",
//            payment_id = 5,
//            appointment_status = false
//        ),
//        Appointment(
//            appointment_id = 6,
//            doctor_id = 6,
//            patient_id = 6,
//            date = "03/27/2024",
//            start_time = "10:00 AM",
//            end_time = "11:00 AM",
//            payment_id = 6,
//            appointment_status = false
//        ),
//        Appointment(
//            appointment_id = 7,
//            doctor_id = 7,
//            patient_id = 7,
//            date = "03/26/2024",
//            start_time = "9:00 AM",
//            end_time = "10:00 AM",
//            payment_id = 7,
//            appointment_status = false
//        )
//    )

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var doctorRepository: DoctorRepository

    private var appointments: MutableList<Appointment> = mutableListOf()
    private var doctorsList: MutableList<Doctor> = mutableListOf()
    private var patient_id : Int = -1

    private lateinit var appointmentAdapter: PatientAppointmentListAdapter

    private val dateFormat = SimpleDateFormat("EEE, MMM d y", Locale.getDefault())
    private val queryDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment_list)

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnPrev = findViewById<Button>(R.id.btnPrev)
        btnNext = findViewById<Button>(R.id.btnNext)


        // Set listeners for navigation buttons
        btnPrev.setOnClickListener { showPreviousDates() }
        btnNext.setOnClickListener { showNextDates() }


        appointmentRepository = AppointmentRepository(this)
        doctorRepository = DoctorRepository(this)

        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        this.patient_id = sharedPreferences.getString("patient_id", "251")?.toInt() ?: 251

        Log.d("PatientSharedPreferences","${this.patient_id}")

        appointmentRepository.getAppointments(patient_id) { retrievedAppointmentsList ->
            // Assuming appointmentsList is a mutable variable accessible in this scope
            appointments = retrievedAppointmentsList
        }

        doctorRepository.getAllDoctors { retrievedDoctorsList ->
            doctorsList = retrievedDoctorsList
            Log.d("DoctorList", " ${retrievedDoctorsList}")
            appointmentAdapter = PatientAppointmentListAdapter(
                appointments,
                doctorsList
            ) { appointment, doctor ->
                // Handle item click
                val intent = Intent(this, PatientAppointmentDetailsActivity::class.java).apply {
                    putExtra(PatientAppointmentDetailsActivity.APPOINTMENT_KEY, appointment as Parcelable)
                    putExtra(PatientAppointmentDetailsActivity.DOCTOR_KEY, doctor as Parcelable)
                }
                startActivity(intent)
            } ?: PatientAppointmentListAdapter(emptyList(), emptyList()) { _, _ -> }

            recyclerView.adapter = appointmentAdapter

            updateDates()

            val todayDate = queryDateFormat.format(Calendar.getInstance().time)
            val filteredAppointments = filterAppointmentsByDate(appointments, todayDate)
            appointmentAdapter.updateList(filteredAppointments)
        }

        Log.d("DoctorList", " ${doctorsList}")


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val dateString = tab.text.toString() // Get the text on the tab
                    val inputFormat = SimpleDateFormat("EEE, MMM d y", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                    try {
                        val date = inputFormat.parse(dateString)
                        val formattedDate = outputFormat.format(date!!)
                        val filteredAppointments = filterAppointmentsByDate(appointments, formattedDate)
//                        Log.d("date and list","${filteredAppointments} and ${appointments}")
                        appointmentAdapter.updateList(filteredAppointments)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

     override fun onResume() {
        super.onResume()
        loadAppointmentsAndDoctors() // Assuming this method loads your data and updates the adapter.
    }

    private fun loadAppointmentsAndDoctors() {
        appointmentRepository.getAppointments(patient_id) { retrievedAppointmentsList ->
            appointments = retrievedAppointmentsList
            updateRecyclerView()
        }

        doctorRepository.getAllDoctors { retrievedDoctorsList ->
            doctorsList = retrievedDoctorsList
            updateRecyclerView()
        }
    }



    private fun updateRecyclerView() {
        appointmentAdapter = PatientAppointmentListAdapter(
            appointments,
            doctorsList
        ) { appointment, doctor ->
            val intent = Intent(this, PatientAppointmentDetailsActivity::class.java).apply {
                putExtra(PatientAppointmentDetailsActivity.APPOINTMENT_KEY, appointment as Parcelable)
                putExtra(PatientAppointmentDetailsActivity.DOCTOR_KEY, doctor as Parcelable)
            }
            startActivity(intent)
        }
        recyclerView.adapter = appointmentAdapter
    }


    private fun updateDates() {
        // Clear existing tabs
        tabLayout.removeAllTabs()

        // Add 3 dates starting from the current date
        for (i in 0 until 3) {
            val date = dateFormat.format(calendar.time)
            tabLayout.addTab(tabLayout.newTab().setText(date))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

    }

    private fun showPreviousDates() {
        // Move the calendar 6 days back to get the starting date for the previous 3 dates
        calendar.add(Calendar.DAY_OF_MONTH, -6)
        updateDates()
    }

    private fun showNextDates() {
        // Move the calendar 3 days forward to get the starting date for the next 3 dates
        calendar.add(Calendar.DAY_OF_MONTH, 0)
        updateDates()

    }

    fun filterAppointmentsByDate(appointments: List<Appointment>, date: String): List<Appointment> {
        val filteredList = mutableListOf<Appointment>()
        for (appointment in appointments) {
            if (appointment.date == date) {
                filteredList.add(appointment)
            }
        }
        return filteredList
    }

    
}