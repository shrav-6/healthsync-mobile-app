package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable
data class Prescription(
    @PropertyName("appointment_id")
    @set:PropertyName("appointment_id")
    var appointmentId: String = "",

    @PropertyName("prescription_id")
    @set:PropertyName("prescription_id")
    var prescriptionId: String = "",

    @PropertyName("medicines")
    @set:PropertyName("medicines")
    var medicines: HashMap<String, Medicine> = hashMapOf(),
) : Serializable

data class Medicine(
    @PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",

    @PropertyName("dosage")
    @set:PropertyName("dosage")
    var dosage: String = "",

    @PropertyName("number_of_days")
    @set:PropertyName("number_of_days")
    var numberOfDays: Int = 0,

    @PropertyName("schedule")
    @set:PropertyName("schedule")
    var schedule: HashMap<String, DaySchedule> = hashMapOf(),
) : Serializable

data class DaySchedule(
    @PropertyName("morning")
    @set:PropertyName("morning")
    var morning: Schedule = Schedule(),

    @PropertyName("afternoon")
    @set:PropertyName("afternoon")
    var afternoon: Schedule = Schedule(),

    @PropertyName("night")
    @set:PropertyName("night")
    var night: Schedule = Schedule(),
) : Serializable

data class Schedule(
    @PropertyName("doctor_said")
    @set:PropertyName("doctor_said")
    var doctorSaid: Boolean = false,

    @PropertyName("patient_took")
    @set:PropertyName("patient_took")
    var patientTook: Boolean = false,
) : Serializable
