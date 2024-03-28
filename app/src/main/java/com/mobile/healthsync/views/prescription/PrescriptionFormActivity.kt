package com.mobile.healthsync.views.prescription

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.healthsync.R
import com.mobile.healthsync.model.DaySchedule
import com.mobile.healthsync.model.Medicine
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Schedule

class PrescriptionFormActivity : AppCompatActivity() {

    private lateinit var appointmentIdEditText: EditText
    private lateinit var prescriptionIdEditText: EditText
    private lateinit var medicineNameEditText: EditText
    private lateinit var medicineDosageEditText: EditText
    private lateinit var medicineDaysEditText: EditText
    private lateinit var morningScheduleCheckbox: CheckBox
    private lateinit var afternoonScheduleCheckbox: CheckBox
    private lateinit var nightScheduleCheckbox: CheckBox
    private lateinit var addMedicineButton: Button
    private lateinit var submitButton: Button
    private lateinit var downloadButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var medicinesList = HashMap<String, Medicine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription_form)

        // Initialize UI elements
        appointmentIdEditText = findViewById(R.id.appointment_id_edit_text)
        prescriptionIdEditText = findViewById(R.id.prescription_id_edit_text)
        medicineNameEditText = findViewById(R.id.medicine_name_edit_text)
        medicineDosageEditText = findViewById(R.id.medicine_dosage_edit_text)
        medicineDaysEditText = findViewById(R.id.medicine_days_edit_text)
        morningScheduleCheckbox = findViewById(R.id.morning_checkbox)
        afternoonScheduleCheckbox = findViewById(R.id.afternoon_checkbox)
        nightScheduleCheckbox = findViewById(R.id.night_checkbox)
        addMedicineButton = findViewById(R.id.add_medicine_button)
        submitButton = findViewById(R.id.submit_button)
        downloadButton = findViewById(R.id.download_button)
        downloadButton.isEnabled = false // Initially disable download button

        // Add medicine dynamically on button click
        addMedicineButton.setOnClickListener {
            val medicineName = medicineNameEditText.text.toString()
            val medicineDosage = medicineDosageEditText.text.toString()
            val medicineDays = medicineDaysEditText.text.toString().toInt()

            val daySchedule = DaySchedule(
                morning = Schedule(doctorSaid = morningScheduleCheckbox.isChecked),
                afternoon = Schedule(doctorSaid = afternoonScheduleCheckbox.isChecked),
                night = Schedule(doctorSaid = nightScheduleCheckbox.isChecked)
            )

            // Create a new Medicine object
            val medicine = Medicine(
                name = medicineName,
                dosage = medicineDosage,
                numberOfDays = medicineDays,
                schedule = daySchedule
            )

            // Generate a unique medicine ID
            val medicineId = "medicine_${medicinesList.size + 1}"
            medicinesList[medicineId] = medicine

            // Display medicine dynamically in the layout
//            displayMedicine(medicine, medicineId)

            // Clear medicine input fields for next entry
            clearMedicineInputFields()
        }

        // Submit prescription to Firebase
        submitButton.setOnClickListener {
            submitPrescription()
        }

    }
    private fun submitPrescription() {
        val appointmentId = appointmentIdEditText.text.toString()
        val prescriptionId = prescriptionIdEditText.text.toString()

        if (TextUtils.isEmpty(appointmentId) || TextUtils.isEmpty(prescriptionId)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

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
                downloadButton.isEnabled = true // Enable download button
                Toast.makeText(this, "Prescription submitted successfully", Toast.LENGTH_SHORT).show()
                println("Prescription document created successfully with ID: $documentId")
            }
            .addOnFailureListener { e ->
                // Handle submission failure
                e.printStackTrace()
                Toast.makeText(this, "Error submitting prescription", Toast.LENGTH_SHORT).show()
            }

    }

    private fun clearMedicineInputFields() {
        // Clear medicine input fields
        medicineNameEditText.text.clear()
        medicineDosageEditText.text.clear()
        medicineDaysEditText.text.clear()
    }
}
