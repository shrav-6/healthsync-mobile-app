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
        val patient_id = sharedPreferences.getString("PatientID", "3")


        //TODO: to receive latest appointment_id based on patient_id and then fetch the prescription_id from the given appointment_id


        val fragment = TodoFragment()

        val bundle = Bundle()
        bundle.putString("patient_id", patient_id.toString())
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().apply {
            // Replace the contents of the fragment_container with the new fragment
            replace(R.id.fragment_container, fragment)
            // Optionally, add the transaction to the back stack
            // addToBackStack(null)
            // Commit the transaction
            commit()
        }

//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        // Replace the fragment_container with TodoFragment
//        val todoFragment = TodoFragment()
//        fragmentTransaction.replace(R.id.fragment_container, todoFragment)
//        fragmentTransaction.addToBackStack(null) // Add to back stack if needed
//        fragmentTransaction.commit()
    }
}