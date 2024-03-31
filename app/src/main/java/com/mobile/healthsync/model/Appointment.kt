package com.mobile.healthsync.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Appointment(var appointment_id: Int = -1,
                       var doctor_id: Int = -1,
                       var patient_id: Int = -1,
                       var date: String = "",
                       var slot_id: Int = -1,
                       var start_time: String = "",
                       var end_time: String = "",
                       var payment_id:Int = -1,
                       var appointment_url: String = "",
                       var appointment_status : Boolean = false) : Parcelable {

}