package com.mobile.healthsync.views.patientDashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var adapter: SlotAdapter
    private var doctor_id : Int = -1
    private var patient_id : Int = -1
    private var slot_id : Int = -1


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 1) {
            // Payment is complete
            Toast.makeText(this, "Payment Complete", Toast.LENGTH_SHORT).show()
            //create appointment record
            finish()
        }
        else if(result.resultCode == 0) {
            // Payment failed
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")//dont know what this is? used for setting colour
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_test)

        appointmentRepository = AppointmentRepository(this)
        doctorRepository = DoctorRepository(this)
        doctor_id = intent.extras?.getInt("doctor_id", -1) ?: -1
        patient_id = intent.extras?.getInt("patient_id", -1) ?: -1

        val now = Calendar.getInstance()
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val formattedDate = format.format(now.time)
        val dateTextView = findViewById<TextView>(R.id.editdate)
        dateTextView.text = formattedDate
        updateslots(formattedDate)

        var searchdatebtn = findViewById<Button>(R.id.searchdate)
        searchdatebtn.setOnClickListener {
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

        var submitbtn = findViewById<Button>(R.id.bookbutton)
        submitbtn.setOnClickListener{
            if(this.adapter.isSlotselected())
            {
                var selectedSlotText: TextView = this.adapter.getselectetSlotText()
                var selectedSlot : Slot = this.adapter.getselectedSlot()
                this.slot_id = selectedSlot.slot_id

                bookAppointment();//after payment is finished /cancelled it comes back here
            }
            else
            {
                Toast.makeText(this, "Slot is not selected!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun bookAppointment() {
//        val intent :Intent = Intent(this, PaymentActivity::class.java)
//        intent.putExtra("doctor_id", this.doctor_id)
//        intent.putExtra("patient_id", this.patient_id)
//        intent.putExtra("slot_id", this.slot_id)
//        startForResult.launch(intent)
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
        var appointmentlist = mutableListOf<Appointment>()
        var slotlist = mutableListOf<Slot>()
        appointmentRepository.getAppointments(doctor_id,selectedDate){ retrievedAppointments ->
            appointmentlist = retrievedAppointments
            doctorRepository.getDoctorAvailability(doctor_id){ retrievedslots ->
                slotlist = retrievedslots
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
                this.adapter = SlotAdapter(slotlist, this)
                recyclerView.adapter = adapter

            }

        }
    }


}