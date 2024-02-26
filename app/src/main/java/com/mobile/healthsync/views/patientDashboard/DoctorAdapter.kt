package com.mobile.healthsync.views.patientDashboard

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.model.Doctor

class DoctorAdapter(
    private val doctors: MutableList<Doctor>
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>()
{
    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}