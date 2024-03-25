package com.mobile.healthsync.model

data class Appointment(
    var appointment_id: Long = 0,
    var date: String = "",
    var doctor_id: Long = 0,
    var patient_id: Long = 0,
    var payment_id: Long = 0,
    var slot_id: Int = 0
)

/*data class Appointment(
    val appointment_id: Long,
    val appointment_status: Boolean,
    val doctor_id: Long,
    val end_time: String, // Assuming this is meant to be a time string. Adjust if it's supposed to be something else.
    val patient_id: Long,
    val payment_id: Long,
    val start_time: String,
    val timestamp: String // Assuming this is the date of the appointment
)*/

