package com.mobile.healthsync.views.doctorProfile

//import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.mobile.healthsync.R


class DoctorProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        // Your data source
        val dataList = ArrayList<String>()
        dataList.add("myItem1")
        dataList.add("myItem2")

        // Your adapter
        val adapter = ArrayAdapter(this, R.layout.activity_doctor_profile, dataList)

        // Your GridView
        val gridView = findViewById<GridView>(R.id.doctorInfo)
        gridView.adapter = adapter

        // Add or remove items as needed and notify the adapter
        dataList.add("Age")
        adapter.notifyDataSetChanged()
    }
}