package com.mobile.healthsync.views.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.healthsync.R
import com.mobile.healthsync.adapters.EventsAdapter

class EventsActivity : AppCompatActivity() {
    private val eventsTabs = arrayOf("Info Sessions", "Donations", "Fundraisers", "Volunteer")
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