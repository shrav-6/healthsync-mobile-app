package com.mobile.healthsync.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.healthsync.views.fragments.DonationsFragment
import com.mobile.healthsync.views.fragments.FundraisersFragment
import com.mobile.healthsync.views.fragments.InfoSessionFragment
import com.mobile.healthsync.views.fragments.VolunteerFragment


class EventsAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return InfoSessionFragment()
            1 -> return DonationsFragment()
            2 -> return FundraisersFragment()
        }
        return VolunteerFragment()
    }
}