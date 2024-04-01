package com.mobile.healthsync.views.doctorDashboard

import android.content.Context
import com.google.firebase.firestore.QuerySnapshot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.BaseActivityForDoctor
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.AppointmentAdapter
import com.mobile.healthsync.model.Appointment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DoctorDashboard : BaseActivityForDoctor() {
    private lateinit var calendarView: LinearLayout
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private var allAppointments: MutableList<Appointment> = mutableListOf()
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

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

        // Get the current month and year
        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearString = monthYearFormat.format(calendar.time)

        // Set the text of the TextView
        val calendarHeaderTextView = findViewById<TextView>(R.id.calendarHeaderTextView)
        calendarHeaderTextView.text = monthYearString
    }

    private fun setupRecyclerView() {
        val initialDate = getCurrentWeekSunday()  // For example, today's date
        adapter = AppointmentAdapter(emptyList(), initialDate)
        appointmentsRecyclerView.adapter = adapter
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadBookings() {
        // Retrieve doctor_id from Shared Preferences
        val sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val doctorId = sharedPreferences.getString("doctor_id", "")?.toLongOrNull() // Assuming doctor_id is stored as a String
        Log.d("DoctorDashboardSharedPref","doctor id in sharedpref ${doctorId}")

        if (doctorId == null) {
            Log.e("Firebase", "Doctor ID not found in Shared Preferences")
            return // Exit if doctor_id is not found or not convertible to Long
        }

        val db = FirebaseFirestore.getInstance()
        // Modify the query to filter by doctor_id
        db.collection("appointments")
            .whereEqualTo("doctor_id", doctorId)
            .get()
            .addOnSuccessListener { documents ->
                allAppointments.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    allAppointments.add(appointment)
                }
                Log.d("Firebase", "Fetched ${allAppointments.size} appointments for doctor_id $doctorId")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading appointments", exception)
            }
    }


    //Load Bookings from Dummy Data
   /* private fun loadBookings() {
        // Dummy data for appointments on 28th March and 30th March
        allAppointments.clear() // Clear existing data

        // Creating dummy appointments
        val dummyAppointments = listOf(
            Appointment(appointment_id = 1, date = "03/28/2024", doctor_id = 101, patient_id = 201, slot_id = 1),
            Appointment(appointment_id = 2, date = "03/28/2024", doctor_id = 102, patient_id = 202, slot_id = 2),
            Appointment(appointment_id = 3, date = "03/30/2024", doctor_id = 103, patient_id = 203, slot_id = 3)
        )

        allAppointments.addAll(dummyAppointments)

        // Assuming you want to display appointments for the current date initially,
        // or you can choose to show for one of the dummy dates by default
        val currentFormattedDate = getCurrentFormattedDate()
        showAppointmentsForDay(currentFormattedDate) // Or replace currentFormattedDate with "28/03/2023" or "30/03/2023" as desired
    }*/



    private fun processAppointmentDocuments(documents: QuerySnapshot) {
        allAppointments.clear()
        for (document in documents) {
            val appointment = document.toObject(Appointment::class.java)
            allAppointments.add(appointment)
        }
        showAppointmentsForDay(getCurrentFormattedDate())
    }

    private fun getCurrentFormattedDate(): String = dateFormat.format(Date())

    private fun setupCalendar() {
        val daysOfWeek = arrayOf("SN", "MO", "TU", "WE", "TH", "FR", "SA")
        val displayFormat = SimpleDateFormat("dd", Locale.getDefault())
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
            val layoutParams = LinearLayout.LayoutParams(
                0, // 0 width for weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Weight of 1 for each dayView to equally divide the space
            )
            dayView.layoutParams = layoutParams
            calendarView.addView(dayView)

            today.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun showAppointmentsForDay(date: String) {
        Log.d("Calendar", "Filtering for date: $date")

        // Format the 'date' to the 'Weekday Date' format
        val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE d", Locale.getDefault())

        // Parse the date string and then format it to the new style
        val formattedDate = try {
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate)
        } catch (e: ParseException) {
            Log.e("DoctorDashboard", "Error parsing the date", e)
            date // Fallback to the original date if parsing fails
        }

        val filteredAppointments = allAppointments.filter {
            it.date == date
        }.sortedBy {
            it.slot_id
        }

        Log.d("Calendar", "Found ${filteredAppointments.size} appointments for $formattedDate")

        // Now pass the formattedDate to the adapter
        adapter.updateAppointments(filteredAppointments, formattedDate)
    }

    private fun getCurrentWeekSunday(): String {
        val calendar = Calendar.getInstance()
        // Set to the first day of the week (Sunday, in most locales)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        // Format the date
        val outputFormat = SimpleDateFormat("EEEE d", Locale.getDefault())
        val sundayDate = outputFormat.format(calendar.time)

        // Now log the formatted date string, not the SimpleDateFormat object
        Log.d("DoctorDashboard", "Sunday Date: $sundayDate")

        return sundayDate
    }



}

