package com.mobile.healthsync.views.events

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.EventsAdapter

/**
 * Activity for displaying various events using ViewPager2 and TabLayout.
 */
class EventsActivity : BaseActivity() {
    private val eventsTabs = arrayOf("Info Sessions", "Donations", "Fundraiser", "Volunteer")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val citiesAdapter = EventsAdapter( supportFragmentManager, lifecycle)
        viewPager.adapter = citiesAdapter

        TabLayoutMediator(tabLayout, viewPager) {
                tab, position -> tab.text = eventsTabs[position]
        }.attach()
    }
}