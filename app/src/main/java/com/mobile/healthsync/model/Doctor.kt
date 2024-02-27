package com.mobile.healthsync.model

data class Doctor(
    var availability: Map<String, Map<String, String>>?,
    var doctor_id: List<Any>,
    var doctor_info: DoctorInfo,
    var doctor_speciality: String?,
    var email: String,
    var password: String
)
data class DoctorInfo(
    var age: String,
    var avg_ratings: String,
    var consultation_fees: String,
    var gender: String,
    var license_expiry: String,
    var license_no: String,
    var name: String,
    var photo: String,
    var years_of_practice: String
)

