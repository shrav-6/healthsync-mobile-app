package com.mobile.healthsync.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyBackgroundService2 : Service() {
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()

        // Initialize Moshi with KotlinJsonAdapterFactory
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        // Initialize Retrofit with MoshiConverterFactory
        val retrofit = Retrofit.Builder()
            .baseUrl("https://vercel-six-chi-73.vercel.app") // Update with your server URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        // Create an instance of your API service
        apiService = retrofit.create(ApiService::class.java)

        // Call the API endpoint to send reminders
        sendReminders()
    }

    private fun sendReminders() {
        apiService.sendReminders().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == "success") {
                        Log.d("MyBackgroundService", "Reminders sent successfully")
                    } else {
                        Log.e("MyBackgroundService", "Failed to send reminders. Response message: ${apiResponse?.message ?: "Unknown error"}")
                    }
                } else {
                    Log.e("MyBackgroundService", "Failed to send reminders. Response code: ${response.code()}")
                }
                stopSelf() // Stop the service after API call
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("MyBackgroundService", "Failed to make API call: ${t.message}")
                stopSelf() // Stop the service on failure
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
