package com.mobile.healthsync.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Medicine

class TodoAdapter(private val medicines: List<Medicine>) :
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
        holder.medicineNameTextView.text = medicine.name
        holder.morningCheckBox.isChecked = medicine.schedule.morning.doctorSaid
        holder.afternoonCheckBox.isChecked = medicine.schedule.afternoon.doctorSaid
        holder.nightCheckBox.isChecked = medicine.schedule.night.doctorSaid

        // Disable checkboxes if doctorSaid is false
        holder.morningCheckBox.isEnabled = medicine.schedule.morning.doctorSaid
        holder.afternoonCheckBox.isEnabled = medicine.schedule.afternoon.doctorSaid
        holder.nightCheckBox.isEnabled = medicine.schedule.night.doctorSaid

        // Update patientTook status when checkboxes are clicked
        holder.morningCheckBox.setOnCheckedChangeListener { _, isChecked ->
            medicine.schedule.morning.patientTook = isChecked
        }
        holder.afternoonCheckBox.setOnCheckedChangeListener { _, isChecked ->
            medicine.schedule.afternoon.patientTook = isChecked
        }
        holder.nightCheckBox.setOnCheckedChangeListener { _, isChecked ->
            medicine.schedule.night.patientTook = isChecked
        }
        Log.d("medicine list updated", medicine.toString())
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineNameTextView: TextView = itemView.findViewById(R.id.text_view_medicine_name)
        val morningCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_morning)
        val afternoonCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_afternoon)
        val nightCheckBox: CheckBox = itemView.findViewById(R.id.check_box_taken_night)

    }
}
