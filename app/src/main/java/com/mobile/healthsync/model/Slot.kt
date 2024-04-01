package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

/**
 * Data class representing a time slot for appointments.
 * @property slot_id The ID of the time slot.
 * @property start_time The start time of the time slot.
 * @property end_time The end time of the time slot.
 */
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

    // Flag to indicate whether the slot is booked
    var isbooked: Boolean = false

    /**
     * Sets the slot as booked.
     */
    fun setAsBooked() {
        this.isbooked = true
    }

    /**
     * Removes the booking from the slot.
     */
    fun removeBooking() {
        this.isbooked = false
    }

    /**
     * Checks if the slot is booked.
     * @return true if the slot is booked, false otherwise.
     */
    fun isBooked() : Boolean {
        return this.isbooked
    }

}
