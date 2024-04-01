package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

/**
 * Data class representing reviews submitted by patients for doctors.
 * @property comment The comment or feedback provided by the patient.
 * @property doctorId The ID of the doctor being reviewed.
 * @property patientId The ID of the patient submitting the review.
 * @property stars The rating given by the patient (in stars, typically ranging from 0 to 5).
 * @property patientName The name of the patient submitting the review.
 */
data class Reviews(
    @get:PropertyName("comment")
    @set:PropertyName("comment")
    var comment: String = "",

    @get:PropertyName("doctor_id")
    @set:PropertyName("doctor_id")
    var doctorId: Int = 0,

    @get:PropertyName("patient_id")
    @set:PropertyName("patient_id")
    var patientId: Int = 0,

    @get:PropertyName("stars")
    @set:PropertyName("stars")
    var stars: Double = 0.0,

    // Additional property for patient name
    var patientName: String = ""
) : Serializable