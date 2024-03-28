package com.mobile.healthsync.views.patientBooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.BookSlotAdapter
import com.mobile.healthsync.model.Availability
import com.mobile.healthsync.model.Slot
import com.mobile.healthsync.repository.AppointmentRepository
import com.mobile.healthsync.repository.DoctorRepository
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingInfoActivity : AppCompatActivity(),OnDateSetListener {

    private val SUCCESS :Int = 1
    private val FAILURE :Int = 0
    private val now = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    private var appointmentRepository : AppointmentRepository;
    private var doctorRepository: DoctorRepository

    private var doctor_id : Int = -1
    private var slot_id :Int = -1
    private lateinit var date : String
    private lateinit var adapter: BookSlotAdapter

    private var appointment_id : Int = 0;
    init {
        //initialising helper classes
        this.appointmentRepository = AppointmentRepository(this)
        this.doctorRepository = DoctorRepository(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        this.doctor_id = intent.extras?.getInt("doctor_id", -1) ?: -1
        val patient_id = intent.extras?.getInt("patient_id", -1) ?: -1
        this.date = fillInitialValues()

        var searchdatebtn = findViewById<Button>(R.id.searchdate)
        searchdatebtn.setOnClickListener {
            val datepicker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.setVersion(DatePickerDialog.Version.VERSION_2);
            datepicker.minDate = now
            var till = Calendar.getInstance()
            till.add(Calendar.MONTH, 2)
            datepicker.maxDate = till
            datepicker.show(supportFragmentManager, "Datepickerdialog")
        }

        var submitbtn = findViewById<Button>(R.id.bookbutton)
        submitbtn.setOnClickListener {
            if(this.adapter.isSlotselected()) {
                var selectedSlot : Slot = this.adapter.getselectedSlot()
                this.slot_id = selectedSlot.slot_id
                handleBooking(patient_id);
            }
            else {
                Toast.makeText(this, "Slot is not selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fillInitialValues() : String{
        val formattedDate = dateFormat.format(now.time)
        val dateTextView = findViewById<TextView>(R.id.editdate)
        dateTextView.text = formattedDate
        updateslots(formattedDate)
        return formattedDate
    }
    private fun handleBooking(patient_id: Int) {
        val intent :Intent = Intent(this, TestActivity::class.java)
        intent.putExtra("doctor_id", this.doctor_id)
        intent.putExtra("patient_id", patient_id)
        intent.putExtra("slot_id", this.slot_id)
        intent.putExtra("date",this.date)
        appointmentRepository.createAppointment(
            this.doctor_id,patient_id,this.slot_id,date,{ appointmentID ->
                this.appointment_id = appointmentID
                updateAfterPayment.launch(intent)
            })
    }

    private val updateAfterPayment = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == this.SUCCESS) { // Payment is complete
            println("Payment Complete")
            val payment_id = result.data?.getIntExtra("payment_id", -1) ?: -1
            appointmentRepository.fixAppointment(this.appointment_id,payment_id)
            finish()
        }
        else if(result.resultCode == this.FAILURE) { // Payment failed
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val selectDate = Calendar.getInstance()
        selectDate.set(Calendar.YEAR, year)
        selectDate.set(Calendar.MONTH, monthOfYear)
        selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        // Apply the format to the date
        val formattedDate = dateFormat.format(selectDate.time)
        val dateTextView = findViewById<TextView>(R.id.editdate)
        dateTextView.text = formattedDate
        this.date = formattedDate
        updateslots(formattedDate)
    }
    fun updateslots(selectedDate : String) {
        appointmentRepository.getAppointments(doctor_id,selectedDate){ retrievedAppointments ->
            doctorRepository.getDoctorAvailability(doctor_id){ retrieved_availabiltiy ->
                val date: Date = dateFormat.parse(selectedDate) ?: Date()
                val week : String = dayFormat.format(date)
                var day_availability : Availability? = retrieved_availabiltiy.get(week)
                val is_available = day_availability?.is_available

                for(appointment in retrievedAppointments) {
                    for(slot in day_availability?.slots!!) {
                        if(appointment.appointment_status == true && slot.slot_id == appointment.slot_id) {
                            slot.setAsBooked()
                            break
                        }
                    }
                }
                if(is_available == true) {
                    val recyclerView = findViewById<RecyclerView>(R.id.slots)
                    this.adapter = BookSlotAdapter(day_availability?.slots!!,this@BookingInfoActivity)

                    recyclerView.adapter = this.adapter
                    recyclerView.layoutManager = GridLayoutManager( this@BookingInfoActivity,2)

                }

            }
        }
    }

}
