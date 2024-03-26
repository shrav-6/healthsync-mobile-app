package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Slot

class AvailabilityAdapter(private val availabilityList: List<Slot>) : RecyclerView.Adapter<AvailabilityAdapter.AvailabilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityViewHolder {
        val availabilityView = LayoutInflater.from(parent.context).inflate(R.layout.doctor_availability_item, parent, false)
        return AvailabilityViewHolder(availabilityView)
    }

    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {
        val availability = availabilityList[position]
        val startTimeString = availability.start_time
        val endTimeString = availability.end_time

        holder.dayOfWeekTextView.text = when (position) {
            0 -> "Monday"
            1 -> "Tuesday"
            2 -> "Wednesday"
            3 -> "Thursday"
            4 -> "Friday"
            5 -> "Saturday"
            6 -> "Sunday"
            else -> ""
        }

        if (startTimeString == "null" || endTimeString == "null" || startTimeString == "NA" || endTimeString == "NA"){
            holder.availabilityCheckbox.visibility = View.VISIBLE
            holder.availabilityCheckbox.isChecked = true
            holder.startTimeLabel.visibility = View.GONE
            holder.startTimePicker.visibility = View.GONE
            holder.endTimeLabel.visibility = View.GONE
            holder.endTimePicker.visibility = View.GONE
        } else {

            // Get the start time
            // Split the time string into hour, minute, and AM/PM components
            val startTimeParts = startTimeString.split(":")
            val startHour = startTimeParts[0].toInt()
            val startMinute = startTimeParts[1].split(" ")[0].toInt()
            val isStartTimeAM = startTimeParts[1].split(" ")[1] == "AM"
            val adjustedStartHour = if (!isStartTimeAM && startHour != 12) startHour + 12 else startHour
            // Set the time on the TimePicker
            holder.startTimePicker.hour = adjustedStartHour
            holder.startTimePicker.minute = startMinute

            // Get the end time
            val endTimeParts = endTimeString.split(":")
            val endHour = endTimeParts[0].toInt()
            val endMinute = endTimeParts[1].split(" ")[0].toInt()
            val isEndTimeAM = endTimeParts[1].split(" ")[1] == "AM"
            val adjustedEndHour = if (!isEndTimeAM && endHour != 12) endHour + 12 else endHour
            holder.endTimePicker.hour = adjustedEndHour
            holder.endTimePicker.minute = endMinute
        }

        val checkBox = holder.availabilityCheckbox

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // If the checkbox is checked, hide the time picker
                holder.startTimeLabel.visibility = View.GONE
                holder.startTimePicker.visibility = View.GONE
                holder.endTimeLabel.visibility = View.GONE
                holder.endTimePicker.visibility = View.GONE
            } else {
                // If the checkbox is unchecked, show the time picker
                holder.startTimeLabel.visibility = View.VISIBLE
                holder.startTimePicker.visibility = View.VISIBLE
                holder.endTimeLabel.visibility = View.VISIBLE
                holder.endTimePicker.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return availabilityList.size
    }

    class AvailabilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeekTextView: TextView = itemView.findViewById(R.id.dayOfTheWeek)
        val startTimeLabel: TextView = itemView.findViewById(R.id.startTime)
        val startTimePicker: TimePicker = itemView.findViewById(R.id.startTimePicker)
        val endTimeLabel: TextView = itemView.findViewById(R.id.endTime)
        val endTimePicker: TimePicker = itemView.findViewById(R.id.endTimePicker)
        val availabilityCheckbox: CheckBox = itemView.findViewById(R.id.availabilityCheckbox)
    }

    fun getAvailabilityList(): List<Slot> {
        return availabilityList
    }

}