package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Ratings

class RatingsAdapter(var ratings : MutableList<Ratings>?) : RecyclerView.Adapter<RatingsAdapter.RatingViewHolder>(){

    inner class RatingViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var starrating : RatingBar = itemView.findViewById(R.id.starrating)
        var ratingtext : TextView = itemView.findViewById(R.id.ratingtext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_item_layout,parent, false)
        return RatingViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ratings!!.size
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.starrating.rating = ratings!![position].stars.toFloat()
        holder.ratingtext.text = ratings!![position].comment
    }
}