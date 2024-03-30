package com.mobile.healthsync.repository

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobile.healthsync.model.Prescription

class ToDoRepository(private val context: Context) {
    private val db: FirebaseFirestore

    init {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
    }

    fun getappointmentAndPrescriptionId(patient_id: Int?, callback: (List<Pair<Int, Int>>?) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("patient_id", patient_id)
            .limit(1)
            .get()
            .addOnCompleteListener { appointmentTask ->
                if (appointmentTask.isSuccessful) {
                    val querySnapshot = appointmentTask.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val appointmentId = document.getLong("appointment_id")!!.toInt()
                        Log.d("in todo: appointmentid", appointmentId.toString())
                        // Now, fetch prescription_id based on appointment_id
                        db.collection("prescriptions")
                            .whereEqualTo("appointment_id", appointmentId)
                            .limit(1)
                            .get()
                            .addOnCompleteListener { prescriptionTask ->
                                if (prescriptionTask.isSuccessful) {
                                    val prescriptionQuerySnapshot = prescriptionTask.result
                                    if (prescriptionQuerySnapshot != null && !prescriptionQuerySnapshot.isEmpty) {
                                        val prescriptionDocument = prescriptionQuerySnapshot.documents[0]
                                        val prescriptionId = prescriptionDocument.getLong("prescription_id")!!.toInt()
                                        Log.d("in todo: prescription_id", prescriptionId.toString())
                                        // Callback with appointment_id and prescription_id
                                        callback(listOf(appointmentId to prescriptionId))
                                    } else {
                                        callback(null) // No prescription found
                                    }
                                } else {
                                    val exceptionMessage = prescriptionTask.exception?.message ?: "Unknown error"
                                    Log.d("Error fetching prescription id:", exceptionMessage)
                                    callback(null) // Error occurred while fetching prescription id
                                }
                            }
                    } else {
                        callback(null) // No appointment found
                    }
                } else {
                    val exceptionMessage = appointmentTask.exception?.message ?: "Unknown error"
                    Log.d("Error fetching appointment id:", exceptionMessage)
                    callback(null) // Error occurred while fetching appointment id
                }
            }
    }


    fun updateMedicinesForPrescription(prescriptionId: Int, updatedMedicines: List<Prescription.Medicine>) {
        // Reference to the specific prescription document
        Log.d("update todo in db","$updatedMedicines\n$prescriptionId")
        var hashMap = HashMap<String, Prescription.Medicine>()

        for ((index, medicine) in updatedMedicines.withIndex()) {
            val key = "medicine_${updatedMedicines.size - index}"
            hashMap[key] = medicine
        }

//        val test2 = hashMapOf("medicine_2" to Prescription.Medicine(
//            name = "Medicine B",
//            dosage = "5 mg",
//            numberOfDays = 10,
//            schedule = Prescription.Medicine.DaySchedule(
//                morning = Schedule(doctorSaid = true, patientTook = false),
//                afternoon = Schedule(doctorSaid = true, patientTook = true),
//                night = Schedule(doctorSaid = false, patientTook = true)
//            )
//        ), medicine_1=Medicine(name=Medicine A, dosage=10mg, numberOfDays=7, schedule=DaySchedule(morning=Schedule(doctorSaid=true, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=true), night=Schedule(doctorSaid=true, patientTook=true)))}

        val testhashMap = hashMapOf(
            "medicine1" to Prescription.Medicine(
                name = "Medicine A",
                dosage = "10mg",
                numberOfDays = 7,
                schedule = Prescription.Medicine.DaySchedule(
                    morning = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = false,
                        patientTook = false
                    ),
                    afternoon = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = false,
                        patientTook = false
                    ),
                    night = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = false,
                        patientTook = false
                    )
                )
            ),
            "medicine2" to Prescription.Medicine(
                name = "Medicine B",
                dosage = "5mg",
                numberOfDays = 10,
                schedule = Prescription.Medicine.DaySchedule(
                    morning = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = true,
                        patientTook = false
                    ),
                    afternoon = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = true,
                        patientTook = true
                    ),
                    night = Prescription.Medicine.DaySchedule.Schedule(
                        doctorSaid = true,
                        patientTook = true
                    )
                )
            )
        )
        val prescription = Prescription(
            appointmentId = 3,
            prescriptionId = 3,
            medicines = hashMap
        )

        Log.d("medicines hashmap", hashMap.toString())
//        db.collection("prescriptions").whereEqualTo("prescription_id", prescriptionId)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val querySnapshot = task.result
//                    if (querySnapshot != null) {
                        //for (document in querySnapshot.documents) {
                            val documentId = "AADQnRLstLT0vMg2u4Dl"
                            Log.d("documentId",documentId)
                            db.collection("prescriptions").document(documentId).set(prescription)
                            .addOnSuccessListener {
                                Log.d("post in data", "Medicines updated successfully in prescription with ID: $prescriptionId")
                            }
                            .addOnFailureListener { e ->
                                Log.e("post in data", "Error updating medicines in prescription with ID: $prescriptionId", e)
                            }
                            //val readPrescription = document.toObject(Prescription::class.java)!!
                            //callback(readPrescription)
                            //return@addOnCompleteListener // Exit the loop after the first document
                        //}
                    //}
//                } else {
//                    Log.d("post in data", "Medicines update error for $prescriptionId")
//
//                    val exceptionMessage = task.exception?.message ?: "Unknown error"
//                    //showToast("Error fetching prescription data: $exceptionMessage")
//                }
                // If no documents found or an error occurred, invoke the callback with an empty Prescription
                //callback(Prescription())
            }
//            .addOnCompleteListener { documents ->
//                for (document in documents) {
//                    // Retrieve the document corresponding to the prescription
//                    Log.d("in loop",document.id)
//                    val prescriptionDocRef = db.collection("prescriptions").document(document.id)
//                    Log.d("documentid",prescriptionDocRef.toString())
//
//                    // Update the 'medicines' key with the new list of medicines
//                    prescriptionDocRef.update("medicines", testhashMap)
//                        .addOnSuccessListener {
//                            Log.d("post in data", "Medicines updated successfully in prescription with ID: $prescriptionId")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("post in data", "Error updating medicines in prescription with ID: $prescriptionId", e)
//                        }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("failed in todo", "Error querying prescriptions for prescription ID: $prescriptionId", e)
//            }
    //}

    fun loadPrescriptionsData(patientId: String) : Prescription {
        //val prescriptionId =
        //val prescriptionRef = db.collection("prescriptions").document(prescriptionId)

        val db = FirebaseFirestore.getInstance()
        lateinit var readPrescription: Prescription

        db.collection("appointments").whereEqualTo("patient_id",patientId.toInt())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Log.d("appointment documents", documents.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("error while reading from appointment table", exception.toString())
            }

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", "1").limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    Log.d("prescription read", document.data.toString())
                    readPrescription = document.toObject(Prescription::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                Log.w("prescription not found", "Error getting documents: ", exception)
            }

        return readPrescription
    }

    fun loadMedicinesData(appointmentId: Int, callback: (List<Prescription.Medicine>?) -> Unit) {
        // Reference to the "doctors" collection
        val db = Firebase.firestore
        Log.d("db", db.toString())
        Log.d("loadPrescriptionData", "Firestore instance obtained")
        //var medicinesList: List<Prescription.Medicine> = emptyList()

        Log.d("app in todo repo",appointmentId.toString())

        db.collection("prescriptions")
            .whereEqualTo("appointment_id", appointmentId).limit(1)
            .get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null) {
                        for (document in querySnapshot.documents) {
                            val prescription = document.toObject(Prescription::class.java)!!
                            val prescriptionID = prescription?.prescriptionId
                            Log.d("prescription",prescription.toString())

                            Log.d("prescriptionID", prescriptionID.toString())

                            // Assuming prescription is a valid Prescription object obtained from Firestore
                            val medicines: HashMap<String, Prescription.Medicine>? = prescription?.medicines

                            //lateinit var medicinesList: List<Medicine>
                            Log.d("medicines", medicines.toString())
                            if (medicines != null && medicines.isNotEmpty()) {
                                val medicinesList = medicines.values.toList()

                                Log.d("medicinesList", medicinesList.toString())
                                callback(medicinesList)
                            }
                        }
                    }
                } else {
                    callback(null)
                }
            }
        //return medicinesList
        //return arrayListOf(Medicine(name="crosine", dosage="4", numberOfDays=5, schedule=DaySchedule(morning=Schedule(doctorSaid=false, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=true), night=Schedule(doctorSaid=true, patientTook=false))), Medicine(name="dolo", dosage="2", numberOfDays=2, schedule= DaySchedule(morning=Schedule(doctorSaid=true, patientTook=false), afternoon=Schedule(doctorSaid=true, patientTook=false), night= Schedule(doctorSaid=false, patientTook=false))))
    }
}