package com.mobile.healthsync.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Prescription.Medicine

class TodoAdapter(private val medicines: List<Medicine>, private val listener: MedicinesUpdateListener) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    interface MedicinesUpdateListener {
        fun onMedicinesUpdated(medicines: List<Medicine>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_check, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.bind(medicine)
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.text_view_medicine_name)
        private val morningCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_morning)
        private val afternoonCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_afternoon)
        private val nightCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_night)

        fun bind(medicine: Medicine) {
            medicineNameTextView.text = medicine.name

            // Set checkbox states based on patientTook status
            morningCheckBox.isChecked = medicine.schedule.morning.patientTook
            afternoonCheckBox.isChecked = medicine.schedule.afternoon.patientTook
            nightCheckBox.isChecked = medicine.schedule.night.patientTook

            // Disable checkboxes if doctorSaid is false
            morningCheckBox.isEnabled = medicine.schedule.morning.doctorSaid
            afternoonCheckBox.isEnabled = medicine.schedule.afternoon.doctorSaid
            nightCheckBox.isEnabled = medicine.schedule.night.doctorSaid

            // Update patientTook status when checkboxes are clicked
            morningCheckBox.setOnCheckedChangeListener { _, isChecked ->
                medicine.schedule.morning.patientTook = isChecked
                listener.onMedicinesUpdated(medicines) // Notify listener of changes
            }
            afternoonCheckBox.setOnCheckedChangeListener { _, isChecked ->
                medicine.schedule.afternoon.patientTook = isChecked
                listener.onMedicinesUpdated(medicines) // Notify listener of changes
            }
            nightCheckBox.setOnCheckedChangeListener { _, isChecked ->
                medicine.schedule.night.patientTook = isChecked
                listener.onMedicinesUpdated(medicines) // Notify listener of changes
            }
        }
    }
}

