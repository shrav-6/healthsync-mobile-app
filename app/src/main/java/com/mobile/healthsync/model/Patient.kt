package com.mobile.healthsync.model;

data class Patient(
        val firstName: String = "",
        val lastName: String = "",
        val age: Int = 0,
        val weight: Double = 0.0,
        val height: Double = 0.0,
        val gender: String = "",
        val bloodType: String = ""
)
