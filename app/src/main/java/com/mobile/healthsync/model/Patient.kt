package com.mobile.healthsync.model;

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

/**
 * Data class representing a Patient.
 * @property email The email of the patient.
 * @property password The password of the patient.
 * @property patientCreated The creation date of the patient.
 * @property patientDetails The details of the patient.
 * @property patient_id The ID of the patient.
 * @property patientUpdated The last update date of the patient.
 * @property rewardPoints The reward points of the patient.
 * @property token The token associated with the patient.
 */
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
    var rewardPoints: Int = 0,

    @get:PropertyName("token")
    @set:PropertyName("token")
    var token: String = ""

) : Serializable {

    /**
     * Data class representing details of a patient.
     * @property age The age of the patient.
     * @property allergies The allergies of the patient.
     * @property gender The gender of the patient.
     * @property height The height of the patient.
     * @property weight The weight of the patient.
     * @property name The name of the patient.
     * @property photo The photo of the patient.
     */
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