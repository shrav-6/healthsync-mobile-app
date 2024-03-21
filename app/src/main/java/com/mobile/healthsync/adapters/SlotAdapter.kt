package com.mobile.healthsync.adapters

import android.app.Activity
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Slot

class SlotAdapter(val slotList : MutableList<Slot>, val activity : Activity): RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    private lateinit var selectedSlotText : TextView
    private lateinit var selectedSlot : Slot

    inner class SlotViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var slottext : TextView = itemView.findViewById(R.id.slotitem)
        init {
            //sets up the edit button on the individual slot
            slottext.setOnClickListener(){
                if(isValid(slotList[adapterPosition])) {
                    if(::selectedSlot.isInitialized)
                    {
                        selectedSlotText.setBackgroundColor(itemView.resources.getColor(android.R.color.transparent))
                    }
                    slottext.setBackgroundColor(itemView.resources.getColor(R.color.mdtp_accent_color))
                    selectedSlotText = slottext
                    selectedSlot = slotList[adapterPosition]
                }
            }
        }
        private fun isValid(slot : Slot) : Boolean {
            return (if(slot.isBooked()) false else true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.slot_layout,parent, false)
        return SlotViewHolder(v)
    }

    override fun getItemCount(): Int {
        return slotList.size
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.slottext.setText(slotList[position].start_time + " - "+ slotList[position].end_time)
        if(slotList[position].isBooked()){
            holder.slottext.setBackgroundColor(activity.resources.getColor(R.color.purple_200))
        }
    }

    fun isSlotselected():Boolean {
        return if(::selectedSlot.isInitialized) true else false
    }

    fun getselectedSlot() : Slot {
        return this.selectedSlot
    }
}

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        }
        else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}
