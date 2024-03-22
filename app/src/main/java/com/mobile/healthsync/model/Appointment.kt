package com.mobile.healthsync.model

data class Appointment(var appointment_id: Int = -1, var doctor_id: Int = -1, var patient_id: Int = -1, var date: String = "", var slot_id: Int = -1) {

    var payment_id:Int = -1
    var appointment_status : Boolean = false

}