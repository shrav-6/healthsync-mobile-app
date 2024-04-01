package com.mobile.healthsync.views.patientDashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R


/**
 * @input: patientId
 */
class PatientToDo : BaseActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_to_do)

        sharedPreferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val patient_id = sharedPreferences.getString("patient_id", "123")
        Log.d("patient_id in todo",patient_id.toString())


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