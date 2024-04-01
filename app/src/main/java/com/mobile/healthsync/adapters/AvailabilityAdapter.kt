package com.mobile.healthsync.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Availability

class AvailabilityAdapter(private val availabilityList: Map<String, Availability>) : RecyclerView.Adapter<AvailabilityAdapter.AvailabilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityViewHolder {
        val availabilityView = LayoutInflater.from(parent.context).inflate(R.layout.doctor_availability_item, parent, false)
        return AvailabilityViewHolder(availabilityView)
    }

    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {

        val day = when (position) {
            0 -> "Monday"
            1 -> "Tuesday"
            2 -> "Wednesday"
            3 -> "Thursday"
            4 -> "Friday"
            5 -> "Saturday"
            6 -> "Sunday"
            else -> ""
        }

        if(day != ""){
            holder.dayOfWeekTextView.text = day

            // if doctor is available, uncheck the check box
            if (availabilityList[day]?.is_available == true){
                holder.availabilityCheckbox.isChecked = false
            } else {
                holder.availabilityCheckbox.isChecked = true
            }
        }

        availabilityList.forEach { (key, value) ->
            Log.d("Key:", key)
            Log.d("Value:", value.is_available.toString())
        }
    }

    override fun getItemCount(): Int {
        return availabilityList.size
    }

    class AvailabilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeekTextView: TextView = itemView.findViewById(R.id.dayOfTheWeek)
        val availabilityCheckbox: CheckBox = itemView.findViewById(R.id.availabilityCheckbox)
    }

    fun getAvailabilityList(): Map<String, Availability> {
        return availabilityList
    }

}