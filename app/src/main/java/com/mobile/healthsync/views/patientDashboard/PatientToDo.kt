package com.mobile.healthsync.views.patientDashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.healthsync.R
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobile.healthsync.model.Prescription


class PatientToDo : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_to_do)

        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("PatientID", "defaultValue").toString()
        Log.d("PatientID", value)

        val patient_id = 3

        //TODO: to receive latest appointment id based on patient id and then fetch the prescription id from the given appointment id


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the fragment_container with TodoFragment
        val todoFragment = TodoFragment()
        fragmentTransaction.replace(R.id.fragment_container, todoFragment)
        fragmentTransaction.addToBackStack(null) // Add to back stack if needed
        fragmentTransaction.commit()
    }
}