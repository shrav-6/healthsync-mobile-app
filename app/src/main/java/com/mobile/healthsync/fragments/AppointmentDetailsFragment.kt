package com.mobile.healthsync.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.repository.PatientRepository
import com.mobile.healthsync.views.prescription.PrescriptionFormActivity
import java.io.File
import java.io.FileOutputStream


class AppointmentDetailsFragment : Fragment() {

    private lateinit var downloadButton: Button
    private lateinit var addPrescriptionButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadButton = view.findViewById(R.id.download_button)
        addPrescriptionButton = view.findViewById(R.id.add_prescription_button)

        // Get appointment and doctor data from arguments
        val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)
        val doctor: Doctor? = arguments?.getParcelable(DOCTOR_KEY)

        downloadButton.setOnClickListener {
            if (appointment != null && doctor != null) {
                downloadPrescriptionPdf(appointment, doctor)
            }
        }

        addPrescriptionButton.setOnClickListener {

            // Get appointment data from arguments
            val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)

            // Create an Intent to start the PrescriptionFormActivity
            val intent = Intent(requireContext(), PrescriptionFormActivity::class.java)

            // Pass the appointment object to the PrescriptionFormActivity
            intent.putExtra("APPOINTMENT_OBJ", appointment)

            // Start the PrescriptionFormActivity
            startActivity(intent)
        }


        // Update UI with appointment and doctor data
        if (appointment != null && doctor != null) {
            view.findViewById<TextView>(R.id.textDate).text = "Date: ${appointment.date}"
            view.findViewById<TextView>(R.id.textTime).text = "Time: ${appointment.start_time} - ${appointment.end_time}"
            view.findViewById<TextView>(R.id.textDoctorName).text = "Doctor: ${doctor.doctor_info.name}"
            view.findViewById<TextView>(R.id.textSpecialty).text = "Specialty: ${doctor.doctor_speciality}"
            // Add more setText() calls for other appointment and doctor details TextViews as needed
        }
    }

    private fun downloadPrescriptionPdf(appointment: Appointment, doctor: Doctor) {
        val firestore = FirebaseFirestore.getInstance()
        println("appointment_id: " + appointment.appointment_id)
        val appointmentId = appointment.appointment_id
        val prescriptionRef = firestore.collection("prescriptions")

            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                println("addOnSuccessListener: " + appointmentId)
                for (document in querySnapshot.documents) {
                    println("for: " + appointmentId)
                    val prescription = document.toObject(Prescription::class.java)
                    if (prescription != null) {
                        println("downloadPrescriptionPdf: $prescription")
                        generatePrescriptionPdf(prescription, appointment.patient_id, doctor)
                    } else {
                        println("else: " + appointmentId)
                        Toast.makeText(
                            requireContext(),
                            "Prescription not found for the given appointment ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("addOnFailureListener: " + appointmentId)
                Toast.makeText(
                    requireContext(),
                    "Error fetching prescription: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                println("Error fetching prescription: ${exception.message}")
            }
    }

    private fun generatePrescriptionPdf(prescription: Prescription, patientId: Int, doctor: Doctor) {
        println("generatePrescriptionPdf: $prescription")
        val doctorName = "Dr. " + doctor.doctor_info.name
        val doctorSpeciality = doctor.doctor_speciality
        val doctorEmail = doctor.email

        val patientRepository = PatientRepository(requireContext())

        patientRepository.getPatientData(patientId.toString()) { patient ->
            if (patient != null) {


                val pdfFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "prescription.pdf"
                )
                // Initialize Document with A4 page size
                val document = Document(PageSize.A4)
                val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
                document.open()

                // Add doctor letterhead
                addDoctorLetterhead(document, doctorName, doctorSpeciality, doctorEmail, patient)

                // Add centered heading for patient name
                val patientHeading = Paragraph("Prescription for Patient: ${patient.patientDetails.name}", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)).apply{
                    alignment = Element.ALIGN_CENTER
                    spacingAfter = 10f // Add spacing after the heading
                }
                document.add(patientHeading)

                // Add prescription details
                addPrescriptionDetails(document, prescription)

                document.close()

                // Show a toast message for successful download
                Toast.makeText(requireContext(), "Prescription downloaded successfully!", Toast.LENGTH_SHORT).show()

                // Open the downloaded PDF
                val pdfUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", pdfFile)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(pdfUri, "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)

            } else {
                // Handle case where patient is null (not found or error occurred)
                println("Patient not found or error occurred")
            }
        }

    }

    private fun addPrescriptionDetails(document: Document, prescription: Prescription) {
        println("addPrescriptionDetails: $prescription")
        // First, check if medicines is not null
        val medicines = prescription.medicines ?: return  // Return early if null

        // Now that we've checked medicines is not null, we can safely use it
        for ((_, medicine) in medicines) {
            // Add medicine name and dosage
            document.add(Paragraph("\n"))
            document.add(Paragraph("Medicine - ${medicine.name}"))
            document.add(Paragraph("Dosage - ${medicine.dosage} Pills"))
            document.add(Paragraph("Number of days - ${medicine.numberOfDays}"))
            document.add(Paragraph("\n"))

            // Add schedule table
            val scheduleTable = PdfPTable(2)
            scheduleTable.widthPercentage = 100f

            // Add schedule table headers
            val scheduleHeaders = arrayOf("Time", "Doctor's Instruction")
            for (header in scheduleHeaders) {
                val cell = Paragraph(header, Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
                scheduleTable.addCell(cell)
            }

            // Add schedule details for each day
            for (day in listOf("morning", "afternoon", "night")) {
                val schedule = when (day) {
                    "morning" -> medicine.schedule.morning
                    "afternoon" -> medicine.schedule.afternoon
                    "night" -> medicine.schedule.night
                    else -> Prescription.Medicine.DaySchedule.Schedule() // This line is technically unnecessary due to the covering of all cases
                }

                val timeCell = Paragraph(day.replaceFirstChar { it.uppercase() }, Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL))
                val doctorSaidCell = Paragraph(if (schedule.doctorSaid) "Take" else "\t-",
                    Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL)
                )

                scheduleTable.addCell(timeCell)
                scheduleTable.addCell(doctorSaidCell)
            }

            document.add(scheduleTable)
        }
    }

    private fun addDoctorLetterhead(
        document: Document,
        doctorName: String,
        doctorSpeciality: String,
        doctorEmail: String,
        patient: Patient?
    ) {
        // Add doctor Details
        document.add(Paragraph(doctorName))
        document.add(Paragraph(doctorSpeciality))
        document.add(Paragraph(doctorEmail))
        document.add(Paragraph("\n"))
        document.add(Paragraph("\n"))

        // Add patient Details
        document.add(Paragraph("Patient Information:"))
        document.add(Paragraph("\n"))
        if (patient != null) {
            document.add(Paragraph("Name: " + patient.patientDetails.name))
            document.add(Paragraph("Age: " + patient.patientDetails.age.toString()))
            document.add(Paragraph("Weight: " + patient.patientDetails.weight.toString()))
            document.add(Paragraph("Height: " + patient.patientDetails.height.toString()))
            document.add(Paragraph("Gender: " + patient.patientDetails.gender))
            document.add(Paragraph("Allergies: " + patient.patientDetails.allergies))
        }
    }

    companion object {
        private const val APPOINTMENT_KEY = "appointment"
        private const val DOCTOR_KEY = "doctor"

        fun newInstance(appointment: Appointment, doctor: Doctor): AppointmentDetailsFragment {
            val fragment = AppointmentDetailsFragment()
            val args = Bundle()
            args.putParcelable(APPOINTMENT_KEY, appointment)
            args.putParcelable(DOCTOR_KEY, doctor)
            fragment.arguments = args
            return fragment
        }
    }
}
