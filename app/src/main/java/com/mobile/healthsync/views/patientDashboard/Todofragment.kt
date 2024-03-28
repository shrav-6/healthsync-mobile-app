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
import com.mobile.healthsync.model.DaySchedule
import com.mobile.healthsync.model.Medicine
//import com.mobile.healthsync.adapters.TodoAdapter
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Schedule
import com.mobile.healthsync.views.signUp.SignupActivity

class TodoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private lateinit var medicinesList: List<Medicine>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_medicines)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val medicinesListf = loadPrescriptionData()
        //Log.d("after function", medicinesList.toString())
        recyclerView.adapter = TodoAdapter(medicinesListf)

        val submitButton: Button = view.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            //TODO: backend to store the new medicinesList for the given prescriptionID
            val intent = Intent(requireContext(), SignupActivity::class.java) //TODO: go to patient dashboard
            intent.putExtra("medicinesList", ArrayList(medicinesList)) // Pass medicinesList to ConfirmationActivity
            startActivity(intent)
        }

        return view
    }

    fun loadPrescriptionData() : List<Medicine> {
        // Reference to the "doctors" collection
        val db = Firebase.firestore
        Log.d("db", db.toString())
        Log.d("loadPrescriptionData", "Firestore instance obtained")

        // TODO: uncomment below code
        /*db.collection("prescriptions")
            .whereEqualTo("appointment_id", "1").limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    Log.d("document read", document.data.toString())
                    val prescription = document.toObject(Prescription::class.java)

                    val prescriptionID = prescription?.prescriptionId
                    Log.d("prescription",prescription.toString())

                    Log.d("prescriptionID", prescriptionID.toString())

                    // Assuming prescription is a valid Prescription object obtained from Firestore
                    val medicines: HashMap<String, Medicine>? = prescription?.medicines
                    //lateinit var medicinesList: List<Medicine>
                    Log.d("medicines", medicines.toString())
                    if (medicines != null && medicines.isNotEmpty()) {
                        medicinesList = medicines.values.toList()

                        Log.d("medicinesList", medicinesList.toString())
                    }
                    // Handle the retrieved prescription
                    //recyclerView.adapter = adapter
                } else {
                    // No prescription found with the given appointment ID
                    Log.d("Prescription read", "No prescription found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("document not found", "Error getting documents: ", exception)
            }*/

        //return medicinesList
        return arrayListOf(Medicine(name="crosine", dosage="4", numberOfDays=5, schedule=DaySchedule(morning=Schedule(doctorSaid=false, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=false), night=Schedule(doctorSaid=true, patientTook=false))), Medicine(name="dolo", dosage="2", numberOfDays=2, schedule= DaySchedule(morning=Schedule(doctorSaid=true, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=false), night= Schedule(doctorSaid=false, patientTook=false))))
    }
}


