package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.TodoAdapter
import com.mobile.healthsync.model.Prescription.Medicine.DaySchedule
import com.mobile.healthsync.model.Prescription.Medicine
//import com.mobile.healthsync.adapters.TodoAdapter
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Prescription.Medicine.DaySchedule.Schedule
import com.mobile.healthsync.repository.PrescriptionRepository
import com.mobile.healthsync.repository.PrescriptionRepository.loadMedicinesData
//import com.mobile.healthsync.repository.PrescriptionRepository.loadPrescriptionData
import com.mobile.healthsync.views.signUp.SignupActivity

class TodoFragment : Fragment() , TodoAdapter.MedicinesUpdateListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private lateinit var medicinesList: List<Medicine>
    lateinit var sharedPreferences: SharedPreferences

    // Variable to store the updated medicines list
    private var updatedMedicinesList: MutableList<Medicine> = mutableListOf()
    private var appointmentId: Int = 1 //TODO: get from db
    private var prescriptionDocId: String = "1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_medicines)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val medicinesListf = loadMedicinesData(appointmentId)
        recyclerView.adapter = TodoAdapter(medicinesListf,  this)

        val submitButton: Button = view.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            // Update the medicines list with the changes made in the adapter
            medicinesList = updatedMedicinesList
            Log.d("Updated Medicines List", updatedMedicinesList.toString())

            PrescriptionRepository.updateMedicinesForPrescription(prescriptionDocId, updatedMedicinesList)

            // go to patient dashboard once submitted
            val intent = Intent(requireContext(), PatientDashboard::class.java)
            intent.putExtra("medicinesList", ArrayList(medicinesList))
            startActivity(intent)
        }

        return view
    }

    override fun onMedicinesUpdated(medicines: List<Medicine>) {
        // Update the updatedMedicinesList whenever medicines are updated in the adapter
        updatedMedicinesList.clear()
        updatedMedicinesList.addAll(medicines)
    }

}


