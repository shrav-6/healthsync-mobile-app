package com.mobile.healthsync.model;

data class Patient(
        var email: String,
        var password: String,
        var patientCreated: String,
        var patientDetails: PatientDetails
)

data class PatientDetails(
        var age: String,
        var allergies: String,
        var gender: String,
        var height: String,
        var weight: String,
        var name: String,
        var photo: String
)

