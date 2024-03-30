package com.mobile.healthsync.views.patientDashboard

import android.content.Context
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
import com.mobile.healthsync.repository.ToDoRepository
//import com.mobile.healthsync.repository.PrescriptionRepository.loadPrescriptionData
import com.mobile.healthsync.views.signUp.SignupActivity

class TodoFragment : Fragment() , TodoAdapter.MedicinesUpdateListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private lateinit var medicinesList: List<Medicine>
    lateinit var sharedPreferences: SharedPreferences

    // Variable to store the updated medicines list
    private var updatedMedicinesList: MutableList<Medicine> = mutableListOf()
    private var appointmentId: Int = 1
    private var prescriptionId: Int = 1
    private var documentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_medicines)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val patientId = arguments?.getString("patient_id")!!.toInt()

        val repo = ToDoRepository(view.context)

        //var medicinesListf = repo.loadMedicinesData(appointmentId)

        repo.getappointmentAndPrescriptionId(patientId) {result ->
            if (result != null) {
                // Iterate through the list of pairs and retrieve appointment_id and prescription_id

                    documentId = result.get(0)
                    appointmentId = result.get(1).toInt()
                    prescriptionId = result.get(2).toInt()
                    Log.d("document ID in fragment:", documentId.toString())
                    Log.d("Appointment ID in fragment:", appointmentId.toString())
                    Log.d("Prescription ID in fragment:", prescriptionId.toString())


                Log.d("before calling load medicines data",appointmentId.toString())
                repo.loadMedicinesData(appointmentId) { medicinesListRead ->

                    //val medicinesListf = medicinesListRead
                    recyclerView.adapter = TodoAdapter(medicinesListRead!!,  this)
                }
            } else {
                Log.d("Error:", "Failed to retrieve appointment and prescription IDs")
            }
        }

//        Log.d("before calling load medicines data",appointmentId.toString())
//        repo.loadMedicinesData(appointmentId) { medicinesListRead ->
//
//            //val medicinesListf = medicinesListRead
//            recyclerView.adapter = TodoAdapter(medicinesListRead!!,  this)
//        }


        val submitButton: Button = view.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            // Update the medicines list with the changes made in the adapter
            medicinesList = updatedMedicinesList
            Log.d("Updated Medicines List", updatedMedicinesList.toString())


            Log.d("prescriptionId before update medicines",prescriptionId.toString())
            repo.updateMedicinesForPrescription(documentId, appointmentId, prescriptionId, updatedMedicinesList)

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


