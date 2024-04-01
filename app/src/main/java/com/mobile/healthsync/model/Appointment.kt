package com.mobile.healthsync.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Data class representing an appointment.
 *
 * @param appointment_id Unique identifier for the appointment.
 * @param doctor_id Unique identifier for the doctor associated with the appointment.
 * @param patient_id Unique identifier for the patient associated with the appointment.
 * @param date Date of the appointment.
 * @param slot_id Unique identifier for the time slot associated with the appointment.
 * @param start_time Start time of the appointment.
 * @param end_time End time of the appointment.
 * @param payment_id Unique identifier for the payment associated with the appointment.
 * @param appointment_url URL related to the appointment.
 * @param appointment_status Boolean indicating the status of the appointment.
 */
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