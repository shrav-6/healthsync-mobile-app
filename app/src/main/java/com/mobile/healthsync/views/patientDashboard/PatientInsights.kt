package com.mobile.healthsync.views.patientDashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Prescription.Medicine
import com.mobile.healthsync.repository.InsightsRepository

/**
 * @input: patientId
 */
class PatientInsights : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_insights)

        val repo = InsightsRepository(this)
        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        val patientId = sharedPreferences.getString("patient_id", "123")!!.toInt()

        //val repo = InsightsRepository(this)
        repo.getPrescriptionForInsights(patientId) { prescriptionRead ->
            Log.d("insights: after function", prescriptionRead.toString())
//            if(prescriptionRead == Prescription()) {
//                Toast.makeText(this,"Patient does not have any prescriptions to view insights", Toast.LENGTH_LONG).show()
//                Log.d("Error:", "Failed to retrieve appointment and prescription IDs")
//                // go to patient dashboard once submitted
//                val intent = Intent(this, PatientDashboard::class.java)
//                intent.putExtra("from", "patient insights")
//                startActivity(intent)
//            } else {
                //create bar chart
                createBarChart(prescriptionRead)
            //}
        }

        // Update the TextViews with the fetched data
        val doctorNameHolder = findViewById<TextView>(R.id.doctorNameHolder)
        val appointmentDateHolder = findViewById<TextView>(R.id.dateHolder)


        repo.getAppointmentDetails(patientId) { appointmentDetails ->
            if (appointmentDetails != null) {
                // Appointment details retrieved successfully
                val (appointmentDate, doctorName) = appointmentDetails
                doctorNameHolder.text = doctorName
                appointmentDateHolder.text = appointmentDate
            } else {
                Log.d("Error:", "Failed to retrieve appointment details")
            }
        }

    }

    private fun createBarChart(prescription: Prescription) {
        val barChart: BarChart = findViewById(R.id.barChart)

        val barEntries: MutableList<BarEntry> = mutableListOf()
        val xAxisLabels = ArrayList<String>()

        for ((index, medicineEntry) in prescription.medicines!!.entries.withIndex()) {
            val medicine: Medicine = medicineEntry.value
            val morningStatus = if (medicine.schedule.morning.patientTook) 1f else 0f
            val afternoonStatus = if (medicine.schedule.afternoon.patientTook) 1f else 0f
            val nightStatus = if (medicine.schedule.night.patientTook) 1f else 0f
            val xValue = index.toFloat()  // Convert index to float
            barEntries.add(BarEntry(xValue, floatArrayOf(morningStatus, afternoonStatus, nightStatus)))
            xAxisLabels.add(medicine.name)
        }

        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.setColors(
            intArrayOf(
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark
            ), applicationContext
        )
        barDataSet.stackLabels = arrayOf("Morning", "Afternoon", "Night")


        val xAxis = barChart.xAxis
        xAxis.labelCount = barEntries.size
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 13f // Set label text size
        //xAxis.textStyle = Typeface.BOLD // Set label text style
        xAxis.granularity = 1f
        xAxis.yOffset = 10f

        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f // Set minimum value for Y-axis
        yAxis.granularity = 1f // Set granularity for Y-axis
        yAxis.textSize = 13f // Set label text size
        //yAxis.textStyle = Typeface.BOLD // Set label text style

        barChart.axisRight.isEnabled = false // Disable right Y-axis

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f // Adjust bar width as needed

        barChart.data = barData
        barChart.description.isEnabled = false // Disable description
        barChart.legend.isEnabled = true // Disable legend
        barChart.legend.textSize = 12f
        barChart.legend.xEntrySpace = 12f
        barChart.legend.xOffset = 1f

        // Set up a listener for bar clicks
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val index = e.x.toInt()
                    val selectedMedicine = prescription.medicines?.values?.elementAtOrNull(index)
                    val morningRecommended = selectedMedicine?.schedule?.morning?.doctorSaid ?: false
                    val afternoonRecommended = selectedMedicine?.schedule?.afternoon?.doctorSaid ?: false
                    val nightRecommended = selectedMedicine?.schedule?.night?.doctorSaid ?: false
                    selectedMedicine?.let { showMedicineDetailsDialog(it, morningRecommended, afternoonRecommended, nightRecommended) }
                }
            }

            override fun onNothingSelected() {
                // Do nothing
            }
        })

        barChart.invalidate()
    }

    private fun showMedicineDetailsDialog(
        medicine: Medicine,
        morningRecommended: Boolean,
        afternoonRecommended: Boolean,
        nightRecommended: Boolean
    ) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Medicine Details")

        val message = "Name: ${medicine.name}\n" +
                "Dosage: ${medicine.dosage}\n" +
                "Number of Days: ${medicine.numberOfDays}\n" +
                "Morning Recommended: ${if (morningRecommended) "Yes" else "No"}\n" +
                "Afternoon Recommended: ${if (afternoonRecommended) "Yes" else "No"}\n" +
                "Night Recommended: ${if (nightRecommended) "Yes" else "No"}"
        dialogBuilder.setMessage(message)

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
}