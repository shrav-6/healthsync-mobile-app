package com.mobile.healthsync.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.EventTypeAdapter
import com.mobile.healthsync.repository.EventsRepository

class DonationsFragment : Fragment() {
    private lateinit var eventsRepository: EventsRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_donations, container, false)
        eventsRepository = EventsRepository(requireContext())

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        eventsRepository.getEventsBySpecificField("type", "donation") { list ->
            recyclerView.adapter = EventTypeAdapter(list)
        }

        return view
    }
}