package com.mobile.healthsync.model

data class Patient(
    val email: String,
    val password: String,
    val patientCreated: String,
    val patientDetails: Map<String, Any?>
)

