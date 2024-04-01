package com.mobile.healthsync.views.prescription

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.MedicineAdapter
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Prescription.Medicine
import com.mobile.healthsync.model.Prescription.Medicine.DaySchedule
import com.mobile.healthsync.model.Prescription.Medicine.DaySchedule.Schedule
import com.mobile.healthsync.repository.PatientRepository
import java.util.Random

/**
 * Activity for creating a prescription form.
 * Author: Zeel Ravalani
 */
class PrescriptionFormActivity : AppCompatActivity() {

    private lateinit var medicineNameEditText: EditText
    private lateinit var medicineDosageEditText: EditText
    private lateinit var medicineDaysEditText: EditText
    private lateinit var morningScheduleCheckbox: CheckBox
    private lateinit var afternoonScheduleCheckbox: CheckBox
    private lateinit var nightScheduleCheckbox: CheckBox
    private lateinit var addMedicineButton: Button
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var medicinesList = HashMap<String, Medicine>()

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription_form)

        // Retrieve the appointment object from the intent
        val appointment: Appointment? = intent.getParcelableExtra("APPOINTMENT_OBJ")

        // Now you can use the appointment object as needed in your activity
        if (appointment != null) {
            // For example, you can access appointment properties like this:
            val appointmentId = appointment.appointment_id

            val patientRepository = PatientRepository(this)

            patientRepository.getPatientWithPatientId(appointment.patient_id) { patient ->
                if (patient != null) {
                    findViewById<TextView>(R.id.textPatientName).text = "Name: ${patient.patientDetails.name}"
                    findViewById<TextView>(R.id.textPatientAge).text = "Age: ${patient.patientDetails.age}"
                    findViewById<TextView>(R.id.textPatientGender).text = "Gender: ${patient.patientDetails.gender}"
                    findViewById<TextView>(R.id.textPatientHeight).text = "Height: ${patient.patientDetails.height}"
                    findViewById<TextView>(R.id.textPatientWeight).text = "Weight: ${patient.patientDetails.weight}"
                    findViewById<TextView>(R.id.textPatientAllergies).text = "Allergies: ${patient.patientDetails.allergies}"

                    // Initialize UI elements
                    medicineNameEditText = findViewById(R.id.medicine_name_edit_text)
                    medicineDosageEditText = findViewById(R.id.medicine_dosage_edit_text)
                    medicineDaysEditText = findViewById(R.id.medicine_days_edit_text)
                    morningScheduleCheckbox = findViewById(R.id.morning_checkbox)
                    afternoonScheduleCheckbox = findViewById(R.id.afternoon_checkbox)
                    nightScheduleCheckbox = findViewById(R.id.night_checkbox)
                    addMedicineButton = findViewById(R.id.add_medicine_button)
                    submitButton = findViewById(R.id.submit_button)

                    // Initialize RecyclerView and its adapter
                    medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
                    medicineAdapter = MedicineAdapter(medicinesList)
                    medicineRecyclerView.apply {
                        adapter = medicineAdapter
                        layoutManager = LinearLayoutManager(this@PrescriptionFormActivity)
                    }

                    // Add medicine dynamically on button click
                    addMedicineButton.setOnClickListener {
                        val medicineName = medicineNameEditText.text.toString()
                        val medicineDosage = medicineDosageEditText.text.toString()
                        val medicineDaysText = medicineDaysEditText.text.toString()

                        // Perform null checks
                        if (medicineName.isNotEmpty() && medicineDosage.isNotEmpty() && medicineDaysText.isNotEmpty()) {
                            val medicineDays = medicineDaysText.toIntOrNull()

                            // Ensure medicineDays is not null
                            if (medicineDays != null) {
                                val isMorningChecked = morningScheduleCheckbox.isChecked
                                val isAfternoonChecked = afternoonScheduleCheckbox.isChecked
                                val isNightChecked = nightScheduleCheckbox.isChecked

                                // Ensure at least one checkbox is checked
                                if (isMorningChecked || isAfternoonChecked || isNightChecked) {
                                    val daySchedule = DaySchedule(
                                        morning = Schedule(doctorSaid = isMorningChecked),
                                        afternoon = Schedule(doctorSaid = isAfternoonChecked),
                                        night = Schedule(doctorSaid = isNightChecked)
                                    )

                                    // Create a new Medicine object
                                    val medicine = Prescription.Medicine(
                                        name = medicineName,
                                        dosage = medicineDosage,
                                        numberOfDays = medicineDays,
                                        schedule = daySchedule
                                    )

                                    // Generate a unique medicine ID
                                    val medicineId = "medicine_${medicinesList.size + 1}"
                                    medicinesList[medicineId] = medicine

                                    // Update RecyclerView adapter with new medicine
                                    updateMedicineAdapter()

                                    // Clear medicine input fields for next entry
                                    clearMedicineInputFields()
                                } else {
                                    // Handle case where none of the checkboxes are checked
                                    Toast.makeText(this, "Please select at least one schedule", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle invalid input for medicine days
                                Toast.makeText(this, "Invalid input for medicine days", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle empty input fields
                            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Submit prescription to Firebase
                    submitButton.setOnClickListener {
                        submitPrescription(appointmentId)
                    }
                } else {
                    // Handle case where patient is null (not found or error occurred)
                    println("Patient not found or error occurred")
                }
            }
        } else {
            println("Error Appointment obj is null")
        }
    }

    /**
     * Updates the RecyclerView adapter with the current medicine list.
     * Author: Zeel Ravalani
     */
    private fun updateMedicineAdapter() {
        medicineAdapter.updateData(medicinesList)
    }

    /**
     * Generates a unique prescription ID.
     * Author: Zeel Ravalani
     */
    private fun generateUniquePrescriptionId(): Int {
        val timestampPart = (System.currentTimeMillis() % 100000).toInt() // Last 5 digits of the current timestamp
        val randomPart = Random().nextInt(900) + 100 // Ensures a 3-digit random number
        return timestampPart * 1000 + randomPart // Combines both parts
    }

    /**
     * Submits the prescription to Firebase Firestore.
     * @param appointmentId The ID of the appointment associated with the prescription.
     * Author: Zeel Ravalani
     */
    private fun submitPrescription(appointmentId: Int) {
        // Generate a unique prescription ID
        val prescriptionId = generateUniquePrescriptionId()

        // Ensure medicinesList is not null and not empty
        if (medicinesList.isNotEmpty()) {
            val prescription = Prescription(
                appointmentId = appointmentId,
                prescriptionId = prescriptionId,
                medicines = medicinesList
            )

            // Save prescription to Firebase Firestore
            db.collection("prescriptions")
                .add(prescription)
                .addOnSuccessListener { documentReference ->
                    // Prescription saved successfully
                    val documentId = documentReference.id
                    Toast.makeText(this, "Prescription submitted successfully", Toast.LENGTH_SHORT).show()
                    println("Prescription document created successfully with ID: $documentId")

                    // Close the current activity and return to the previous fragment
                    finish()
                }
                .addOnFailureListener { e ->
                    // Handle submission failure
                    e.printStackTrace()
                    Toast.makeText(this, "Error submitting prescription", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where medicinesList is empty
            Toast.makeText(this, "No medicines added to the prescription", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Clears the input fields for adding a new medicine.
     * Author: Zeel Ravalani
     */
    private fun clearMedicineInputFields() {
        // Clear medicine input fields
        medicineNameEditText.text.clear()
        medicineDosageEditText.text.clear()
        medicineDaysEditText.text.clear()
    }
}
