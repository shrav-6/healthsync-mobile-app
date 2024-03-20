package com.mobile.healthsync.model

import com.mobile.healthsync.views.patientDashboard.BookingTestActivity

data class Slot(var slot_id: Int =0, var start_time: String ="", var end_time : String="") {

    private var isbooked = false;
    fun setAsBooked()
    {
        this.isbooked = true
    }

    fun removeBooking()
    {
        this.isbooked = false
    }

    fun isBooked() : Boolean
    {
        return this.isbooked
    }
}