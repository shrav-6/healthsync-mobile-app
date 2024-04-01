package com.mobile.healthsync.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.healthsync.views.events.fragments.DonationFragment
import com.mobile.healthsync.views.events.fragments.FundraisersFragment
import com.mobile.healthsync.views.events.fragments.InfoSessionFragment
import com.mobile.healthsync.views.events.fragments.VolunteerFragment

/**
 * Adapter class for managing fragments in the ViewPager2 for events.
 *
 * @param fragmentManager The FragmentManager instance to interact with fragments.
 * @param lifecycle The Lifecycle instance to manage the lifecycle of fragments.
 */
class EventsAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {

    /**
     * Returns the total number of fragments managed by the adapter.
     *
     * @return The total number of fragments.
     */
    override fun getItemCount(): Int {
        return 4
    }

    /**
     * Creates and returns a new fragment instance based on the specified position.
     *
     * @param position The position of the fragment to be created.
     * @return A new instance of the fragment based on the position.
     */
    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return InfoSessionFragment()
            1 -> return DonationFragment()
            2 -> return FundraisersFragment()
        }
        return VolunteerFragment()
    }
}