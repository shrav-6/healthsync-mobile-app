package com.mobile.healthsync.services

import retrofit2.Call
import retrofit2.http.POST

data class ApiResponse(
    val status: String,
    val message: String
)

interface ApiService {
    @POST("/send-notifications")
    fun sendNotifications(): Call<ApiResponse>

    @POST("/send-reminders")
    fun sendReminders(): Call<ApiResponse>
}
