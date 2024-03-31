package com.mobile.healthsync.model

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

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