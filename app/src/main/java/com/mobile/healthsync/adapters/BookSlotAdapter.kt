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

class BookSlotAdapter(val slotList : MutableList<Slot>, val activity : Activity): RecyclerView.Adapter<BookSlotAdapter.SlotViewHolder>() {

    private lateinit var selectedSlotCard : CardView
    private lateinit var selectedSlot : Slot

    inner class SlotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var slottext : TextView = itemView.findViewById(R.id.slotitem)
        var slotcard : CardView = itemView.findViewById(R.id.slotcard)
        init {
            //sets up the edit button on the individual slot
            slotcard.setOnClickListener(){
                if(isValid(slotList[adapterPosition])) {
                    if(::selectedSlot.isInitialized)
                    {
                        selectedSlotCard.setBackgroundColor(itemView.resources.getColor(android.R.color.transparent))
                    }
                    slotcard.setBackgroundColor(itemView.resources.getColor(R.color.teal_200))
                    selectedSlotCard = slotcard
                    selectedSlot = slotList[adapterPosition]
                }
            }
        }
        private fun isValid(slot : Slot) : Boolean {
            return (if(slot.isBooked()) false else true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bookingslot_layout,parent, false)
        return SlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        return slotList.size
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.slottext.setText(slotList[position].start_time + " - "+ slotList[position].end_time)
        if(slotList[position].isBooked()){
            holder.slotcard.setBackgroundColor(activity.resources.getColor(R.color.purple_200))
        }
    }

    fun isSlotselected():Boolean {
        return if(::selectedSlot.isInitialized) true else false
    }

    fun getselectedSlot() : Slot {
        return this.selectedSlot
    }
}
