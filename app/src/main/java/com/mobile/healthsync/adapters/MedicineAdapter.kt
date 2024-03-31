package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Prescription.Medicine

/**
 * Adapter class for displaying medicines in a RecyclerView.
 * Author: Zeel Ravalani
 */
class MedicineAdapter(private var medicines: HashMap<String, Medicine>) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    /**
     * ViewHolder class for holding medicine item views.
     * Author: Zeel Ravalani
     */
    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.medicineNameTextView)
        val medicineDosage: TextView = itemView.findViewById(R.id.medicineDosageTextView)
        val medicineDays: TextView = itemView.findViewById(R.id.medicineDaysTextView)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines.values.toList()[position]
        holder.medicineName.text = "Name: ${medicine.name}"
        holder.medicineDosage.text = "Dosage: ${medicine.dosage}"
        holder.medicineDays.text = "Days: ${medicine.numberOfDays}"

        holder.removeButton.setOnClickListener {
            println("removeButton Clicked meds: $medicines")
            println("removeButton Clicked: $medicine")
            println("removeButton Clicked: " + medicine.name)
            // Remove the medicine from the list and update RecyclerView
            // Get the list of keys (medicine IDs)
            val medicineIds = medicines.keys.toList()

            // Get the key (medicine ID) at the current position
            val medicineIdToRemove = medicineIds[position]

            // Remove the medicine using the obtained key
            medicines.remove(medicineIdToRemove)

            println("removeButton Clicked meds: $medicines")
            updateMedicineAdapter()
        }
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    /**
     * Updates the data in the adapter with new medicines.
     * @param newMedicines The new list of medicines.
     * Author: Zeel Ravalani
     */
    fun updateData(newMedicines: HashMap<String, Medicine>) {
        medicines = newMedicines
        notifyDataSetChanged()
    }

    /**
     * Updates the RecyclerView adapter with the current medicine list.
     * Author: Zeel Ravalani
     */
    private fun updateMedicineAdapter() {
        updateData(medicines)
    }
}
