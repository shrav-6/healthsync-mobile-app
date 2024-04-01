package com.mobile.healthsync.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Data class representing a Doctor entity.
 *
 * @param doctor_id The unique identifier of the doctor.
 * @param availability The availability schedule of the doctor.
 * @param doctor_info Information about the doctor.
 * @param email The email of the doctor.
 * @param password The password of the doctor.
 * @param doctor_speciality The specialty of the doctor.
 * @param token The authentication token of the doctor.
 */
@Parcelize
data class Doctor(
    @get:PropertyName("doctor_id")
    @set:PropertyName("doctor_id")
    var doctor_id: Int = -1,

    @get:PropertyName("availability")
    @set:PropertyName("availability")
    var availability: Map<String,Availability>? = null,

    @get:PropertyName("doctor_info")
    @set:PropertyName("doctor_info")
    var doctor_info: DoctorInfo = DoctorInfo(),

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("password")
    @set:PropertyName("password")
    var password: String = "",

    @get:PropertyName("doctor_speciality")
    @set:PropertyName("doctor_speciality")
    var doctor_speciality: String = "General Medicine",

    @get:PropertyName("token")
    @set:PropertyName("token")
    var token: String = ""

) : Serializable, Parcelable {

    /**
     * Data class representing additional information about the doctor.
     *
     * @param age The age of the doctor.
     * @param avg_ratings The average ratings of the doctor.
     * @param consultation_fees The consultation fees of the doctor.
     * @param gender The gender of the doctor.
     * @param license_expiry The expiry date of the doctor's license.
     * @param license_no The license number of the doctor.
     * @param name The name of the doctor.
     * @param photo The photo URL of the doctor.
     * @param years_of_practice The years of practice of the doctor.
     */
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
