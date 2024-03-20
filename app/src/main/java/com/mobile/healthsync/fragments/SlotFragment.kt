package com.mobile.healthsync.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.healthsync.R
import java.util.Date

/**
 * A simple [Fragment] subclass.
 * Use the [SlotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SlotFragment(val doctor_id: Int, val date : Date) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slot, container, false)
    }
}