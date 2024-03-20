package com.mobile.healthsync.views.patientDashboard

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.GridSpacingItemDecoration
import com.mobile.healthsync.adapters.SlotAdapter
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Slot
import com.mobile.healthsync.repository.AppointmentRepository
import com.mobile.healthsync.repository.DoctorRepository
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates

class BookingTestActivity : AppCompatActivity(),OnDateSetListener {

    private lateinit var appointmentRepository : AppointmentRepository;
    private lateinit var doctorRepository: DoctorRepository
    private var doctor_id by Delegates.notNull<Int>()

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")//dont know what this is? used for setting colour
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_test)

        appointmentRepository = AppointmentRepository(this)
        doctorRepository = DoctorRepository(this)
        doctor_id = intent.extras?.getInt("doctor_id")!!

        var searchdatebtn = findViewById<Button>(R.id.searchdate)
        searchdatebtn.setOnClickListener {
            val now = Calendar.getInstance()
            val datepicker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.show(supportFragmentManager, "Datepickerdialog")
            datepicker.setVersion(DatePickerDialog.Version.VERSION_2);
            datepicker.accentColor = R.color.purple_200

            datepicker.minDate = now
            var later = Calendar.getInstance()
            later.add(Calendar.MONTH, 3)
            datepicker.maxDate = later
        }
        val now = Calendar.getInstance()
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val formattedDate = format.format(now.time)
        val dateTextView = findViewById<TextView>(R.id.editdate)
        dateTextView.text = formattedDate
        updateslots(formattedDate)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Define the format
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        // Apply the format to the date
        val formattedDate = format.format(calendar.time)

        // Now you can use formattedDate
        val dateTextView = findViewById<TextView>(R.id.editdate)
        dateTextView.text = formattedDate

        updateslots(formattedDate)

    }

    fun updateslots(selectedDate : String)
    {
        // If you want to print the formatted date string
        var appointmentlist = mutableListOf<Appointment>()
        var slotlist = mutableListOf<Slot>()
        appointmentRepository.getAppointments(doctor_id,selectedDate){ retrievedAppointments ->
            appointmentlist = retrievedAppointments
            println("Testingaaaa" +appointmentlist)
            doctorRepository.getDoctorAvailability(doctor_id){ retrievedslots ->
                slotlist = retrievedslots
                println("Testingaaaa Testingaaaa" +slotlist)
                for(appointment in appointmentlist)
                {
                    var slot_id = appointment.slot_id
                    for(slot in slotlist)
                    {
                        if(slot.slot_id == slot_id)
                        {
                            slot.setAsBooked()
                            break
                        }
                    }
                }

                //update the slot fragment here
                //loads users' expenses inside the recyclerview
                val recyclerView = findViewById<RecyclerView>(R.id.slots)
                recyclerView.layoutManager = GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
                val spacingInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
                recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
                //create expenselist of user using SQLLite
                val adapter = SlotAdapter(slotlist)
                recyclerView.adapter = adapter

            }

        }
    }


}