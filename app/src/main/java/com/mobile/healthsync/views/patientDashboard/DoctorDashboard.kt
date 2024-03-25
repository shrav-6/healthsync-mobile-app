package com.mobile.healthsync.views.patientDashboard

import com.google.firebase.firestore.QuerySnapshot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

class DoctorDashboard : AppCompatActivity() {
    private lateinit var calendarView: LinearLayout
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private var allAppointments: MutableList<Appointment> = mutableListOf()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        initializeViews()
        setupRecyclerView()
        loadBookings()
        setupCalendar()
    }

    private fun initializeViews() {
        calendarView = findViewById(R.id.weeklyCalendarView)
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(emptyList())
        appointmentsRecyclerView.adapter = adapter
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadBookings() {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments")
            .get()
            .addOnSuccessListener { documents ->
                allAppointments.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    allAppointments.add(appointment)
                }
                Log.d("Firebase", "Fetched ${allAppointments.size} appointments")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading appointments", exception)
            }
    }


    private fun processAppointmentDocuments(documents: QuerySnapshot) {
        allAppointments.clear()
        for (document in documents) {
            val appointment = document.toObject(Appointment::class.java)
            allAppointments.add(appointment)
        }
        showAppointmentsForDay(getCurrentFormattedDate())
    }

    private fun getCurrentFormattedDate(): String = dateFormat.format(Date())

    /*private fun setupCalendar() {
        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val today = Calendar.getInstance()
        for (i in 0 until 7) {
            val dayView = LayoutInflater.from(this).inflate(R.layout.calendar_day_view, null)
            val dateTextView: TextView = dayView.findViewById(R.id.dateTextView)
            val dayLabelTextView: TextView = dayView.findViewById(R.id.dayLabelTextView)

            today.add(Calendar.DAY_OF_MONTH, if (i == 0) 0 else 1)

            val date = dateFormat.format(today.time)
            dateTextView.text = date
            dayLabelTextView.text = daysOfWeek[today.get(Calendar.DAY_OF_WEEK) - 1]

            dayView.setOnClickListener { showAppointmentsForDay(date) }
            calendarView.addView(dayView)
        }
    }*/

    private fun setupCalendar() {
        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val displayFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        val queryFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val today = Calendar.getInstance()

        // Ensure calendar starts on the correct day of the week
        today.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in 0 until 7) {

            val dayView = LayoutInflater.from(this).inflate(R.layout.calendar_day_view, calendarView, false)
            dayView.isClickable = true
            dayView.setFocusable(true)

            val dateTextView: TextView = dayView.findViewById(R.id.dateTextView)
            val dayLabelTextView: TextView = dayView.findViewById(R.id.dayLabelTextView)

            val displayDate = displayFormat.format(today.time)
            val queryDate = queryFormat.format(today.time)
            dateTextView.text = displayDate
            dayLabelTextView.text = daysOfWeek[i]

            dayView.setOnClickListener {
                Log.d("CalendarView", "Day clicked: $queryDate")
                showAppointmentsForDay(queryDate)
            }
            calendarView.addView(dayView)

            today.add(Calendar.DAY_OF_MONTH, 1)
        }
    }


    /* private fun showAppointmentsForDay(date: String) {
         val filteredAppointments = allAppointments.filter { appointment ->
             appointment.date == date
         }
         adapter.updateAppointments(filteredAppointments)
     }*/

    private fun showAppointmentsForDay(date: String) {
        Log.d("Calendar", "Filtering for date: $date")
        val filteredAppointments = allAppointments.filter { appointment ->
            appointment.date == date
        }.sortedBy { it.slot_id }
        Log.d("Calendar", "Found ${filteredAppointments.size} appointments for $date")
        adapter.updateAppointments(filteredAppointments)
    }



}

