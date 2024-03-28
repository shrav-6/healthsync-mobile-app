package com.mobile.healthsync.views.prescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Schedule
import java.io.File
import java.io.FileOutputStream

class PrescriptionDownloadActivity : AppCompatActivity() {

    private lateinit var downloadButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription_download)

        downloadButton = findViewById(R.id.download_button)

        downloadButton.setOnClickListener {
            val appointmentId = "1" // Replace with the actual appointment ID
            downloadPrescriptionPdf(appointmentId)
        }
    }

    private fun downloadPrescriptionPdf(appointmentId: String) {
        val firestore = FirebaseFirestore.getInstance()
        println("appointment_id: " + appointmentId)
        val prescriptionRef = firestore.collection("prescriptions")
            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val prescription = document.toObject(Prescription::class.java)
                    if (prescription != null) {
                        generatePrescriptionPdf(prescription)
                    } else {
                        Toast.makeText(
                            this,
                            "Prescription not found for the given appointment ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error fetching prescription: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                println("Error fetching prescription: ${exception.message}")
            }
    }

    private fun generatePrescriptionPdf(prescription: Prescription) {
        val doctorName = "Dr. John Doe"
        val clinicName = "Your Clinic Name"
        val clinicAddress = "Clinic Address"
        val clinicContact = "Clinic Contact"
        val patientName = "John Wick"

        val pdfFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "prescription.pdf"
        )
        // Initialize Document with A4 page size
        val document = Document(PageSize.A4)
        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()

        // Add doctor letterhead
        addDoctorLetterhead(document, doctorName, clinicName, clinicAddress, clinicContact)

        // Add centered heading for patient name
        val patientHeading = Paragraph("Prescription for Patient: $patientName", Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)).apply{
            alignment = Element.ALIGN_CENTER
            spacingAfter = 10f // Add spacing after the heading
        }
        document.add(patientHeading)

        // Add prescription details
        addPrescriptionDetails(document, prescription)

        document.close()

        // Show a toast message for successful download
        Toast.makeText(this, "Prescription downloaded successfully!", Toast.LENGTH_SHORT).show()

        // Open the downloaded PDF
        val pdfUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", pdfFile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun addPrescriptionDetails(document: Document, prescription: Prescription) {
        // Iterate over medicines and add their details to the PDF
        for ((_, medicine) in prescription.medicines) {
            // Add medicine name and dosage
            document.add(Paragraph("\n"))
            document.add(Paragraph("${medicine.name} - ${medicine.dosage}"))
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
                val schedule: Schedule = when (day) {
                    "morning" -> medicine.schedule.morning
                    "afternoon" -> medicine.schedule.afternoon
                    "night" -> medicine.schedule.night
                    else -> Schedule() // Default Schedule if key not found
                }

                val timeCell = Paragraph(day.capitalize(), Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL))
                val doctorSaidCell = Paragraph(if (schedule.doctorSaid) "Take" else "\t-",
                    Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL))

                scheduleTable.addCell(timeCell)
                scheduleTable.addCell(doctorSaidCell)
            }

            document.add(scheduleTable)
        }
    }


    private fun addDoctorLetterhead(
        document: Document,
        doctorName: String,
        clinicName: String,
        clinicAddress: String,
        clinicContact: String
    ) {
        // Add doctor name
        document.add(Paragraph(doctorName))

        // Add clinic details
        document.add(Paragraph(clinicName))
        document.add(Paragraph(clinicAddress))
        document.add(Paragraph(clinicContact))
    }
}