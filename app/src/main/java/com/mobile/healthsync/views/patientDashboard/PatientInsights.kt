package com.mobile.healthsync.views.patientDashboard

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.healthsync.R
import com.mobile.healthsync.model.DaySchedule

import android.R.attr.name
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView
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

import com.mobile.healthsync.model.Medicine

import com.mobile.healthsync.model.Prescription
import com.mobile.healthsync.model.Schedule


class PatientInsights : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_insights)

        //TODO: get the latest appointment's prescription id
        //for now assumming it to be 1

        val prescription_id = "1"

        //TODO: read the prescription for the given prescription id
        //for now using expected data
        val prescription = Prescription(
            appointmentId = "1",
            prescriptionId = "1",
            medicines = hashMapOf(
                "medicine_2" to Medicine(
                    name = "crosine",
                    dosage = "4",
                    numberOfDays = 5,
                    schedule = DaySchedule(
                        morning = Schedule(doctorSaid = false, patientTook = false),
                        afternoon = Schedule(doctorSaid = false, patientTook = false),
                        night = Schedule(doctorSaid = true, patientTook = true)
                    )
                ),
                "medicine_1" to Medicine(
                    name = "dolo",
                    dosage = "2",
                    numberOfDays = 2,
                    schedule = DaySchedule(
                        morning = Schedule(doctorSaid = true, patientTook = true),
                        afternoon = Schedule(doctorSaid = true, patientTook = true),
                        night = Schedule(doctorSaid = true, patientTook = true)
                    )
                ),
                "medicine_3" to Medicine(
                    name = "acnedap",
                    dosage = "2",
                    numberOfDays = 2,
                    schedule = DaySchedule(
                        morning = Schedule(doctorSaid = true, patientTook = false),
                        afternoon = Schedule(doctorSaid = true, patientTook = true),
                        night = Schedule(doctorSaid = true, patientTook = true)
                    )
                )
            )
        )

        //TODO: get doctor name and appointment date
        //for now using dummy data
        val doctorName = "Shelly"
        val appointmentDate = "16/01/2024"

        // Update the TextViews with the fetched data
        val doctorNameHolder = findViewById<TextView>(R.id.doctorNameHolder)
        val appointmentDateHolder = findViewById<TextView>(R.id.dateHolder)

        doctorNameHolder.text = doctorName
        appointmentDateHolder.text = appointmentDate


        //create bar chart
        createBarChart(prescription)

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


        //for (i in prescription.medicines!!.keys) {
        //    xAxisLabels.add(i)
        //}
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
        barChart.legend.textSize = 13f
        barChart.legend.xEntrySpace = 10f
        //barChart.legend.yOffset = 0.5f

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