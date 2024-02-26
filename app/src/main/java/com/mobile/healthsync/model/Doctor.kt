package com.mobile.healthsync.model

data class Doctor(
    var availability: Map<String, Map<String, String>>?,
    var doctor_id: Int,
    var doctor_info: DoctorInfo,
    var doctor_speciality: String?,
    var email: String,
    var password: String
) {
    data class DoctorInfo(
        var age: Int,
        var avg_ratings: Double,
        var consultation_fees: Double,
        var gender: String,
        var license_expiry: String,
        var license_no: Int,
        var name: String,
        var photo: String,
        var years_of_practice: Int
    )
}
