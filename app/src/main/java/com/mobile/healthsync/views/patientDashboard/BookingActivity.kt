package com.mobile.healthsync.views.patientDashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import java.util.Calendar

class BookingActivity : AppCompatActivity() {

//    var date_format = SimpleDateFormat("dd MMM, YYYY", Locale.US)
//    var time_format = SimpleDateFormat("hh : mm a", Locale.US)
    private var bookingSlot : Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                bookingSlot.set(Calendar.YEAR, year)
                bookingSlot.set(Calendar.MONTH, month)
                bookingSlot.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                //val date = date_format.format(bookingSlot.time)

                val timePicker = TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT , TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    bookingSlot.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    bookingSlot.set(Calendar.MINUTE, minute)
                    //val time = time_format.format(bookingSlot.time)

                },now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false)
                timePicker.show()

            },now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
    }
}