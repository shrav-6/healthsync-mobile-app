package com.mobile.healthsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor

class DoctorAdapter(
    private val doctors: MutableList<Doctor>
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>()
{
    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_doctor_list,
                parent,
                false
                )
        )
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        var currDoctor = doctors[position]

        val tvDoctorName = holder.itemView.findViewById<TextView>(R.id.tvDoctorName)
        val tvExperience = holder.itemView.findViewById<TextView>(R.id.tvExperience)
        val tvSpeciality = holder.itemView.findViewById<TextView>(R.id.tvSpeciality)
        val tvConsultationFee = holder.itemView.findViewById<TextView>(R.id.tvConsultationFee)

        tvDoctorName.text = currDoctor.doctor_info.name
        tvExperience.text = currDoctor.doctor_info.years_of_practice.toString()
        tvSpeciality.text = currDoctor.doctor_info.doctor_speciality
        tvConsultationFee.text = "${currDoctor.doctor_info.consultation_fees}$/consultation"
//        tvDoctorName.text = currDoctor.email
//        tvExperience.text = currDoctor.email
//        tvSpeciality.text = currDoctor.email
//        tvConsultationFee.text = "${currDoctor.doctor_id}$ per consultation"
    }

    override fun getItemCount(): Int {
        return doctors.size
    }


}