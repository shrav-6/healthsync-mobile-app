package com.mobile.healthsync.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.views.patientBooking.BookingInfoActivity

class DoctorAdapter(
    private val doctors: MutableList<Doctor>,
    var patient_id: Int,
    var activity: Activity
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>()
{
    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var bookaptbtn: Button = itemView.findViewById(R.id.bookAppointmentButton)
        init {

            bookaptbtn.setOnClickListener(){
                val intent  = Intent(activity, BookingInfoActivity::class.java)
                intent.putExtra("doctor_id",doctors[adapterPosition].doctor_id)
                intent.putExtra("patient_id",patient_id)
                activity.startActivity(intent)
            }
        }

    }

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
        tvSpeciality.text = currDoctor.doctor_speciality
        tvConsultationFee.text = "${currDoctor.doctor_info.consultation_fees}$/consultation"
//        tvDoctorName.text = currDoctor.email
//        tvExperience.text = currDoctor.email
//        tvSpeciality.text = currDoctor.email
//        tvConsultationFee.text = "${currDoctor.doctor_id}$ per consultation"
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    fun updateDoctorsList(newList: List<Doctor>) {
        doctors.clear()
        doctors.addAll(newList)
        notifyDataSetChanged()
    }
}