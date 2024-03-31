package com.mobile.healthsync.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.mobile.healthsync.model.Reviews
import com.mobile.healthsync.repository.PatientRepository
import com.mobile.healthsync.views.prescription.PrescriptionFormActivity
import java.io.File
import java.io.FileOutputStream
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import kotlin.random.Random
import androidx.appcompat.app.AppCompatActivity


/**
 * Fragment class responsible for displaying appointment details.
 * Allows users to download prescription PDF, add prescriptions, and submit reviews.
 * Author: Dev Patel
 */
class AppointmentDetailsFragment : Fragment() {

    private lateinit var downloadButton: Button
    private lateinit var addPrescriptionButton: Button
    private lateinit var btnOpenReviewPopup: Button
    private lateinit var videoCallButton: Button
    private lateinit var selectedUrlTextView: TextView
    private lateinit var db: FirebaseFirestore
    private val TAG = "RatingAndReviewsActivity"
    private val meetLinks = listOf(
        "https://meet.google.com/opk-pbqh-pqv",
        "https://meet.google.com/unq-ktip-voj",
        "https://meet.google.com/vpe-cinr-pgf",
        "https://meet.google.com/koz-wooa-dvf",
        "https://meet.google.com/die-dpuo-ios",
        "https://meet.google.com/eqv-ture-bfv",
        "https://meet.google.com/fjr-ugxs-oii",
        "https://meet.google.com/bif-hpvc-ezc"
    )


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
        btnOpenReviewPopup = view.findViewById(R.id.btnReview)
        videoCallButton = view.findViewById(R.id.initiate_vc_button)
        selectedUrlTextView = view.findViewById<TextView>(R.id.selectedUrlTextView)

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

        btnOpenReviewPopup.setOnClickListener {

            // Get appointment data from arguments
            val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)

            showReviewPopup(it, appointment) // 'it' refers to the clicked button
        }

        videoCallButton.setOnClickListener {
            // Get appointment data from arguments
            val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)

            initiateVideoCall(appointment, selectedUrlTextView)
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

    private fun initiateVideoCall(appointment: Appointment?, textView: TextView) {
        if (appointment != null) {
            val appointmentUrl = appointment.appointment_url

            if (appointmentUrl.isNullOrEmpty()) {
                val meetLink = generateRandomMeetLink()
                openMeetLinkInBrowser(meetLink)
                saveAppointment(meetLink, appointment)
                setClickableLink(meetLink, textView)
            } else {
                openMeetLinkInBrowser(appointmentUrl)
                setClickableLink(appointmentUrl, textView)
            }
        } else {
            Toast.makeText(requireContext(), "No appointment data available", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateRandomMeetLink(): String {
        val randomIndex = Random.nextInt(meetLinks.size)
        return meetLinks[randomIndex]
    }

    private fun openMeetLinkInBrowser(meetLink: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetLink))
        startActivity(browserIntent)
    }

    private fun saveAppointment(meetLink: String, appointment: Appointment?) {
        val firestore = FirebaseFirestore.getInstance()
        val appointmentId = appointment?.appointment_id

        if (appointmentId != null) {
            val appointmentRef = firestore.collection("appointments").document(appointmentId.toString())
            appointmentRef.update("appointment_url", meetLink)
                .addOnSuccessListener {
                    Log.d("AppointmentDetailsFragment", "Appointment URL updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("AppointmentDetailsFragment", "Error updating appointment URL: $e")
                }
        } else {
            Log.e("AppointmentDetailsFragment", "No appointment ID available")
        }
    }

    private fun setClickableLink(link: String, textView: TextView) {
        val text = if (link.isNotEmpty()) "Appointment Link: $link" else "Appointment Link: "
        val spannableString = SpannableString(text)
        if (link.isNotEmpty()) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openMeetLinkInBrowser(link)
                }
            }
            spannableString.setSpan(clickableSpan, 16, text.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }

    override fun onPause() {
        super.onPause()
        // App goes to the background
    }

    override fun onResume() {
        super.onResume()
        // App comes back to the foreground
        // Show toast message saying "Appointment done"
        Toast.makeText(requireContext(), "Appointment call is finished", Toast.LENGTH_SHORT).show()

        // Update appointment_url to an empty string in Firestore
        val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)
        val appointmentId = appointment?.appointment_id
        if (appointmentId != null && isResumed) { // Check if fragment is resumed
            val firestore = FirebaseFirestore.getInstance()
            val appointmentRef = firestore.collection("appointments").document(appointmentId.toString())
            appointmentRef.update("appointment_url", "")
                .addOnSuccessListener {
                    Log.d("AppointmentDetailsFragment", "Appointment URL updated to empty string successfully")
                    // Update text view with an empty string
                    selectedUrlTextView.text = ""
                }
                .addOnFailureListener { e ->
                    Log.e("AppointmentDetailsFragment", "Error updating appointment URL: $e")
                }
        } else {
            Log.e("AppointmentDetailsFragment", "No appointment ID available")
        }
    }


    /**
     * Shows a popup dialog for submitting reviews.
     * @param view The view from which the popup dialog is triggered.
     * @param appointment The appointment for which the review is being submitted.
     * Author: Zeel Ravalani
     */
    fun showReviewPopup(view: View, appointment: Appointment?) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.review_popup, null)
        dialogBuilder.setView(dialogView)

        val etComment = dialogView.findViewById<EditText>(R.id.etComment)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        btnSubmit.setOnClickListener {
            val comment = etComment.text.toString()
            val stars = ratingBar.rating.toDouble()
            if (appointment != null) {
                val review =  Reviews(comment, appointment.doctor_id, appointment.patient_id, stars)
                saveReviewToFirebase(review)
            }

            alertDialog.dismiss()
        }
    }

    /**
     * Saves the review to Firebase Firestore.
     * @param review The review object to be saved.
     * Author: Zeel Ravalani
     */
    private fun saveReviewToFirebase(review: Reviews) {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()
        db.collection("reviews").add(review)
            .addOnSuccessListener { documentReference ->
                // Review saved successfully
                Log.d(TAG, "Review saved successfully with ID: ${documentReference.id}")
                Toast.makeText(requireContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle errors
                Log.e(TAG, "Error saving review", e)
                Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Downloads the prescription PDF for the given appointment.
     * @param appointment The appointment for which the prescription is being downloaded.
     * @param doctor The doctor associated with the appointment.
     * Author: Zeel Ravalani
     */
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

    /**
     * Generates the prescription PDF for the given prescription.
     * @param prescription The prescription object.
     * @param patientId The ID of the patient associated with the prescription.
     * @param doctor The doctor associated with the prescription.
     * Author: Zeel Ravalani
     */
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

    /**
     * Adds prescription details to the PDF document.
     * @param document The PDF document.
     * @param prescription The prescription object.
     * Author: Zeel Ravalani
     */
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

    /**
     * Adds doctor and patient details to the PDF document.
     * @param document The PDF document.
     * @param doctorName The name of the doctor.
     * @param doctorSpeciality The specialty of the doctor.
     * @param doctorEmail The email of the doctor.
     * @param patient The patient associated with the prescription.
     * Author: Zeel Ravalani
     */
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

        /**
         * Creates a new instance of AppointmentDetailsFragment.
         * @param appointment The appointment object.
         * @param doctor The doctor object.
         * @return A new instance of AppointmentDetailsFragment.
         * Author: Dev Patel
         */
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
