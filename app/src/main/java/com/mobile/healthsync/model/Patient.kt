package com.mobile.healthsync.model;

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Patient(
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("password")
    @set:PropertyName("password")
    var password: String = "",

    @get:PropertyName("patient_created")
    @set:PropertyName("patient_created")
    var patientCreated: String = "",

    @get:PropertyName("patient_details")
    @set:PropertyName("patient_details")
    var patientDetails: PatientDetails = PatientDetails(),

    @get:PropertyName("patient_id")
    @set:PropertyName("patient_id")
    var patient_id: Int = 0,

    @get:PropertyName("patient_updated")
    @set:PropertyName("patient_updated")
    var patientUpdated: String = "",

    @get:PropertyName("reward_points")
    @set:PropertyName("reward_points")
    var rewardPoints: Int = 0
) : Serializable {
    data class PatientDetails(
        @get:PropertyName("age")
        @set:PropertyName("age")
        var age: Int = 0,

        @get:PropertyName("allergies")
        @set:PropertyName("allergies")
        var allergies: String? = null,

        @get:PropertyName("gender")
        @set:PropertyName("gender")
        var gender: String = "",

        @get:PropertyName("height")
        @set:PropertyName("height")
        var height: Int = 0,

        @get:PropertyName("weight")
        @set:PropertyName("weight")
        var weight: Int = 0,

        @get:PropertyName("name")
        @set:PropertyName("name")
        var name: String = "",

        @get:PropertyName("photo")
        @set:PropertyName("photo")
        var photo: String? = null
    ) : Serializable
}



