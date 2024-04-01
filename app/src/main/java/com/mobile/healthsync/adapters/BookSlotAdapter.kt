package com.mobile.healthsync.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Slot

/**
 * Adapter class for managing slots in a RecyclerView.
 *
 * @param slotList List of slots to be displayed.
 * @param activity The activity associated with the adapter.
 */
class BookSlotAdapter(val slotList : List<Slot>, val activity : Activity): RecyclerView.Adapter<BookSlotAdapter.SlotViewHolder>() {

    // Selected slot card view
    private lateinit var selectedSlotCard : CardView
    // Selected slot
    private lateinit var selectedSlot : Slot

    /**
     * View holder class for individual slots.
     *
     * @param itemView The view for an individual slot item.
     */
    inner class SlotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        // TextView for displaying slot details
        var slottext : TextView = itemView.findViewById(R.id.slotitem)
        // CardView for the slot item
        var slotcard : CardView = itemView.findViewById(R.id.slotcard)
        init {
            // Click listener for the slot item
            slotcard.setOnClickListener(){
                if(isValid(slotList[adapterPosition])) {
                    // Reset the background color of the previously selected slot
                    if(::selectedSlot.isInitialized)
                    {
                        selectedSlotCard.setCardBackgroundColor(itemView.resources.getColor(android.R.color.white))
                    }
                    // Set the background color of the selected slot
                    slotcard.setCardBackgroundColor(itemView.resources.getColor(R.color.pastelBlue))

                    // Update the selected slot and its card view
                    selectedSlotCard = slotcard
                    selectedSlot = slotList[adapterPosition]
                }
            }
        }

        /**
         * Checks if a slot is valid (i.e., not already booked).
         *
         * @param slot The slot to be checked.
         * @return True if the slot is valid, false otherwise.
         */
        private fun isValid(slot : Slot) : Boolean {
            return (if(slot.isBooked()) false else true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        // Inflate the layout for an individual slot item
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bookingslot_layout,parent, false)
        return SlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        // Return the total number of slots
        return slotList.size
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        // Bind slot data to the view holder
        holder.slottext.setText(slotList[position].start_time + " - "+ slotList[position].end_time)
        // Set background color based on slot booking status
        if(slotList[position].isBooked()){
            holder.slotcard.setCardBackgroundColor(activity.resources.getColor(R.color.purple_200))
        }
    }

    /**
     * Checks if a slot is selected.
     *
     * @return True if a slot is selected, false otherwise.
     */
    fun isSlotselected():Boolean {
        return if(::selectedSlot.isInitialized) true else false
    }

    /**
     * Gets the selected slot.
     *
     * @return The selected slot.
     */
    fun getselectedSlot() : Slot {
        return this.selectedSlot
    }
}
