package com.mobile.healthsync.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Doctor(
    @get:PropertyName("doctor_id")
    @set:PropertyName("doctor_id")
    var doctor_id: Int = -1,

    @get:PropertyName("availability")
    @set:PropertyName("availability")
    var availability: List<Slot>? = null,

    @get:PropertyName("doctor_info")
    @set:PropertyName("doctor_info")
    var doctor_info: DoctorInfo = DoctorInfo(),

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("password")
    @set:PropertyName("password")
    var password: String = "",

    var doctor_speciality: String = "General Medicine"
) : Serializable {

    data class DoctorInfo(
        @get:PropertyName("age")
        @set:PropertyName("age")
        var age: Int = 0,

        @get:PropertyName("avg_ratings")
        @set:PropertyName("avg_ratings")
        var avg_ratings: Double = 0.0,

        @get:PropertyName("consultation_fees")
        @set:PropertyName("consultation_fees")
        var consultation_fees: Double = 0.0,

        @get:PropertyName("gender")
        @set:PropertyName("gender")
        var gender: String = "",

        @get:PropertyName("license_expiry")
        @set:PropertyName("license_expiry")
        var license_expiry: String = "",

        @get:PropertyName("license_no")
        @set:PropertyName("license_no")
        var license_no: String = "",

        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String = "",

        @get:PropertyName("photo")
        @set:PropertyName("photo")
        var photo: String? = null,

        @get:PropertyName("years_of_practice")
        @set:PropertyName("years_of_practice")
        var years_of_practice: Int = 0
    ) : Serializable
}
