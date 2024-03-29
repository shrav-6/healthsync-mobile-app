package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Slot(
    @get:PropertyName("slot_id")
    @set:PropertyName("slot_id")
    var slot_id: Int = 0,

    @get:PropertyName("start_time")
    @set:PropertyName("start_time")
    var start_time: String = "",

    @get:PropertyName("end_time")
    @set:PropertyName("end_time")
    var end_time: String = ""
) : Serializable {

    var isbooked: Boolean = false
    fun setAsBooked() {
        this.isbooked = true
    }

    fun removeBooking() {
        this.isbooked = false
    }

    fun isBooked() : Boolean {
        return this.isbooked
    }

}
