package com.mobile.healthsync.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.mobile.healthsync.R.id.initiate_vc_button
import com.mobile.healthsync.model.Appointment
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.model.Patient
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Reviews
import com.mobile.healthsync.repository.PatientRepository
import com.mobile.healthsync.views.patientDashboard.PatientAppointmentListActivity
import com.mobile.healthsync.views.prescription.PrescriptionFormActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

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
    private lateinit var cancelAppointmentButton: Button
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
    private var isAppointmentUrlSet = false
    private var onResumeCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_details, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadButton = view.findViewById(R.id.download_button)
        addPrescriptionButton = view.findViewById(R.id.add_prescription_button)
        btnOpenReviewPopup = view.findViewById(R.id.btnReview)
        videoCallButton = view.findViewById(initiate_vc_button)
        selectedUrlTextView = view.findViewById(R.id.selectedUrlTextView)
        cancelAppointmentButton = view.findViewById(R.id.cancel_appointment_button)

        // Get appointment and doctor data from arguments
        val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)
        val doctor: Doctor? = arguments?.getParcelable(DOCTOR_KEY)

        val sharedPreferences = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val isDoctor = sharedPreferences.getBoolean("isDoctor", false)

        if (isDoctor) {

            addPrescriptionButton.visibility = View.VISIBLE
            downloadButton.visibility = View.VISIBLE
            videoCallButton.visibility = View.VISIBLE
            btnOpenReviewPopup.visibility = View.GONE
            cancelAppointmentButton.visibility = View.GONE

        } else {
            btnOpenReviewPopup.visibility = View.VISIBLE
            cancelAppointmentButton.visibility = View.VISIBLE
            downloadButton.visibility = View.VISIBLE
            videoCallButton.visibility = View.GONE
            addPrescriptionButton.visibility = View.GONE
        }

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

        cancelAppointmentButton.setOnClickListener {
            // Implement the logic to cancel the appointment
            cancelAppointment(appointment)
        }
        
        // Update UI with appointment and doctor data
        if (appointment != null && doctor != null) {
            view.findViewById<TextView>(R.id.textDate).text = "Date: ${appointment.date}"
            view.findViewById<TextView>(R.id.textTime).text = "Time: ${appointment.start_time}"
            view.findViewById<TextView>(R.id.textDoctorName).text = "Doctor: ${doctor.doctor_info.name}"
            view.findViewById<TextView>(R.id.textSpecialty).text = "Speciality: ${doctor.doctor_speciality}"
            setClickableLink("${appointment.appointment_url}", view.findViewById<TextView>(R.id.selectedUrlTextView))
        }
    }

    /**
     * Initiates a video call with the provided appointment data.
     * If the appointment has a URL, opens it in the browser; otherwise, generates a random URL,
     * saves it to Firestore, and updates the UI.
     *
     * @param appointment The appointment data.
     * @param textView The TextView to display the clickable link.
     */
    private fun initiateVideoCall(appointment: Appointment?, textView: TextView) {
        if (appointment != null) {
            val appointmentUrl = appointment.appointment_url

            if (!isAppointmentUrlSet) { // Check if the URL is not set yet
                if (appointmentUrl.isNullOrEmpty()) {
                    Log.d("AppointmentDetailsFragment", "Appointment URL is being updated for the first time")
                    // Generate random meet link if URL is not provided
                    val meetLink = generateRandomMeetLink()
                    // Open meet link in browser
                    openMeetLinkInBrowser(meetLink)
                    // Save meet link to Firestore and update UI
                    saveAppointment(meetLink, appointment)
                    // Set the meet link as a clickable link in the TextView
                    setClickableLink(meetLink, textView)
                    isAppointmentUrlSet = true // Set the flag indicating URL is set
                } else {
                    Log.d("AppointmentDetailsFragment", "Appointment URL present. Opening meeting link directly.")
                    // Open provided meet link in browser
                    openMeetLinkInBrowser(appointmentUrl)
                    // Set the meet link as a clickable link in the TextView
                    setClickableLink(appointmentUrl, textView)
                    isAppointmentUrlSet = true // Set the flag indicating URL is set
                }
            } else {
                Log.d("AppointmentDetailsFragment", "Appointment URL already set. Skipping.")
            }
        }
    }

    /**
     * Generates a random meet link from a predefined list.
     *
     * @return A randomly selected meet link.
     */
    private fun generateRandomMeetLink(): String {
        val randomIndex = Random.nextInt(meetLinks.size)
        return meetLinks[randomIndex]
    }

    /**
     * Opens the provided meet link in the default browser.
     *
     * @param meetLink The meet link to be opened.
     */
    private fun openMeetLinkInBrowser(meetLink: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetLink))
        startActivity(browserIntent)
    }

    /**
     * Saves the generated meet link to Firestore as the appointment URL.
     *
     * @param meetLink The meet link to be saved.
     * @param appointment The appointment data.
     */
    /**
     * Saves the generated meet link to Firestore as the appointment URL.
     *
     * @param meetLink The meet link to be saved.
     * @param appointment The appointment data.
     */
    private fun saveAppointment(meetLink: String, appointment: Appointment?) {
        val db = FirebaseFirestore.getInstance()
        val appointmentId = appointment?.appointment_id

        if (appointmentId != null) {
            Log.d("appointment_id",appointmentId.toString())
            val appointmentRef = db.collection("appointments").whereEqualTo("appointment_id",appointmentId.toLong())
            appointmentRef.limit(1)
                .get()
                .addOnCompleteListener { appointmentTask ->
                    if (appointmentTask.isSuccessful) {
                        val querySnapshot = appointmentTask.result
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            val document = querySnapshot.documents[0]
                            val documentId = document.id

                            Log.d("documentId",documentId)

                            db.collection("appointments").document(documentId)
                                .update("appointment_url", meetLink)
                                .addOnSuccessListener {
                                    Log.d("AppointmentDetailsFragment", "Appointment URL updated successfully")
                                    // Reset isAppointmentUrlSet if the URL is set to ""
                                    if (meetLink.isEmpty()) {
                                        isAppointmentUrlSet = false
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AppointmentDetailsFragment", "Error updating appointment URL: $e")
                                }
                        }
                        }
                    }
//                    else {
//                        Log.d("error while saving appointment url","in update url")
//                    }

//            appointmentRef.update("appointment_url", meetLink)
//                .addOnSuccessListener {
//                    Log.d("AppointmentDetailsFragment", "Appointment URL updated successfully")
//                    // Reset isAppointmentUrlSet if the URL is set to ""
//                    if (meetLink.isEmpty()) {
//                        isAppointmentUrlSet = false
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("AppointmentDetailsFragment", "Error updating appointment URL: $e")
//                }
        } else {
            Log.e("AppointmentDetailsFragment", "No appointment ID available")
        }
    }


    /**
     * Sets a clickable link in the provided TextView.
     *
     * @param link The link to be displayed.
     * @param textView The TextView in which the link is displayed.
     */
    private fun setClickableLink(link: String, textView: TextView) {
        val text = if (link.isNotEmpty()) "Appointment Link: $link" else "Appointment Link: N.A."
        val spannableString = SpannableString(text)
        if (link.isNotEmpty()) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Open the link in the browser when clicked
                    openMeetLinkInBrowser(link)
                }
            }
            // Set the clickable span in the text
            spannableString.setSpan(clickableSpan, 16, text.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        // Set the text with clickable link in the TextView
        textView.apply {
            movementMethod = LinkMovementMethod.getInstance()
            setText(spannableString, TextView.BufferType.SPANNABLE)
        }
    }

    /**
     * Overrides the `onPause` method of the superclass to handle backgrounding of the fragment.
     */
    override fun onPause() {
        super.onPause()
        onResumeCount++
        // App goes to the background
    }

    /**
     * Overrides the `onResume` method of the superclass to handle foregrounding of the fragment.
     * Additionally, shows a toast message indicating the completion of the appointment call.
     * Updates the appointment URL to an empty string in Firestore.
     */
    override fun onResume() {
        super.onResume()
        onResumeCount++
        if (onResumeCount > 1) {
            // App comes back to the foreground (not the first time)
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
                        // Save meet link to Firestore and update UI
                        saveAppointment("", appointment)
                        // Update text view with an empty string
                        view?.findViewById<TextView>(R.id.selectedUrlTextView)?.text = "Appointment URL: N.A."
                    }
                    .addOnFailureListener { e ->
                        Log.e("AppointmentDetailsFragment", "Error updating appointment URL: $e")
                    }
            } else {
                Log.e("AppointmentDetailsFragment", "No appointment ID available")
            }
        }
    }

    /**
     * Resets the flags tracking whether the appointment URL is set and if the fragment has been resumed once.
     * This method is called when the fragment is being destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        isAppointmentUrlSet = false
        onResumeCount = 0
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with downloading the PDF
                // Retrieve appointment and doctor data again
                val appointment: Appointment? = arguments?.getParcelable(APPOINTMENT_KEY)
                val doctor: Doctor? = arguments?.getParcelable(DOCTOR_KEY)
                if (appointment != null && doctor != null) {
                    downloadPrescriptionPdf(appointment, doctor)
                }
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(
                    requireContext(),
                    "Permission denied, cannot download prescription",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }*/

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


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with PDF download
                // Your existing PDF download logic goes here
                // ...
                Toast.makeText(requireContext(), "Downloading prescription PDF...", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, show a message
                Toast.makeText(
                    requireContext(),
                    "Permission denied, cannot download prescription",
                    Toast.LENGTH_SHORT
                ).show()
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
        Log.d("println", "${appointment.appointment_id}  :   sdjhv")
        val appointmentId = appointment.appointment_id
        val prescriptionRef = firestore.collection("prescriptions")

            .whereEqualTo("appointment_id", appointmentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("println", "${appointment.appointment_id}  :   addOnSuccessListener")
                println("addOnSuccessListener: " + appointmentId)
                Log.d("println", "${querySnapshot.documents.size}  :   addOnSuccessListener")
                for (document in querySnapshot.documents) {
                    Log.d("println", "${appointment.appointment_id}  :   in for")
                    println("for: " + appointmentId)
                    val prescription = document.toObject(Prescription::class.java)
                    if (prescription != null) {
                        Log.d("println", "${appointment.appointment_id}  :  downloadPrescriptionPdf")
                        println("downloadPrescriptionPdf: $prescription")

                        // Check permissions before downloading the PDF
                            generatePrescriptionPdf(prescription, appointment.patient_id, doctor)
                    } else {
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

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to request required permissions
    /*private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }*/



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

        patientRepository.getPatientWithPatientId(patientId) { patient ->
            if (patient != null) {
                Log.d("println", "${patient.patientDetails.name}  :   sdjhv")
                val pdfFile = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
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
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(pdfUri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
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

    private fun cancelAppointment(appointment: Appointment?) {
        appointment?.let { appt ->
            val db = FirebaseFirestore.getInstance()

            // Step 1: Fetch payment record to retrieve the amount paid as a string
            db.collection("payments")
                .whereEqualTo("payment_id", appt.payment_id)
                .get()
                .addOnSuccessListener { payments ->
                    if (payments.documents.isNotEmpty()) {
                        val paymentDocument = payments.documents.first()
                        val amountPaidString = paymentDocument.getString("amount_paid") ?: "$0.00"
                        // Extract the numeric value from the amountPaidString
                        val amountPaid = amountPaidString.drop(1).toDoubleOrNull() ?: 0.0

                        // Proceed to delete the payment
                        val paymentRef = paymentDocument.reference
                        paymentRef.delete().addOnSuccessListener {
                            Log.d(TAG, "Payment successfully deleted")

                            // Step 2: Delete the appointment
                            // Assuming appt.appointment_id is an Int and is available
                            val appointmentId = appt.appointment_id

                            // Step 2: Confirm the appointment exists before attempting to delete
                            db.collection("appointments")
                                .whereEqualTo("appointment_id", appointmentId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (documents != null && !documents.isEmpty) {
                                        // Assuming appointment_id uniquely identifies the document
                                        val documentSnapshot = documents.documents[0]
                                        val appointmentDocRef = db.collection("appointments").document(documentSnapshot.id)

                                        // Proceed to delete the appointment
                                        appointmentDocRef.delete().addOnSuccessListener {
                                            Log.d(TAG, "Appointment successfully deleted")
                                            // Continue with next steps, e.g., updating reward points
                                        }.addOnFailureListener { e ->
                                            Log.e(TAG, "Error deleting appointment document: ", e)
                                        }
                                    } else {
                                        Log.d(TAG, "No appointment found with id: $appointmentId")
                                    }
                                }.addOnFailureListener { e ->
                                    Log.e(TAG, "Error fetching appointment document: ", e)
                                }

                            // Step 3: Fetch patient to update reward points
                                db.collection("patients").whereEqualTo("patient_id", appt.patient_id)
                                    .get()
                                    .addOnSuccessListener { patients ->
                                        if (patients.documents.isNotEmpty()) {
                                            val patientDocument = patients.documents.first()
                                            val currentPoints = patientDocument.getDouble("reward_points") ?: 0.0
                                            val newPoints = currentPoints + amountPaid

                                            // Step 4: Update the reward points for the patient
                                            patientDocument.reference
                                                .update("reward_points", newPoints)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "Reward points updated to $newPoints")
                                                    // Show confirmation message
                                                    Toast.makeText(context, "Booking Cancelled Successfully. $newPoints reward points have been added to your account.", Toast.LENGTH_LONG).show()
                                                    showBookingCancelledDialog(newPoints)
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.e(TAG, "Error updating reward points: ", e)
                                                }
                                        } else {
                                            Log.d(TAG, "No such patient found")
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.e(TAG, "Error fetching patient: ", e)
                                    }
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error deleting payment: ", e)
                        }
                    } else {
                        Log.d(TAG, "No such payment found")
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching payment record: ", e)
                }
        }
    }

    private fun showBookingCancelledDialog(newPoints: Double) {
        activity?.let { act ->
            AlertDialog.Builder(act).apply {
                setTitle("Booking Cancelled Successfully")
                setMessage("$newPoints reward points have been added to your account.")
                setPositiveButton("Okay") { dialog, which ->
                    // Instead of just dismissing the dialog, start the PatientAppointmentListActivity
                    val intent = Intent(context, PatientAppointmentListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears the activity stack up to PatientAppointmentListActivity
                    startActivity(intent)
                }
                create().show()
            }
        }
    }




    companion object {
        private const val APPOINTMENT_KEY = "appointment"
        private const val DOCTOR_KEY = "doctor"
        //private const val PERMISSION_REQUEST_CODE = 100

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
