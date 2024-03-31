package com.mobile.healthsync

import android.content.Intent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mobile.healthsync.views.doctorProfile.DoctorProfile
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.patientProfile.PatientProfile

open class BaseActivityForDoctor : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_toolbar_doctor, null) as DrawerLayout
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.activity_content1)
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)


        setupToolbar() // Call the setupToolbar method to initialize drawer navigation
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.activity_container1)
        val navView = findViewById<NavigationView>(R.id.navigationView1)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                /*R.id.dashboard -> {
                    startActivity(Intent(this, PatientDashboard::class.java))
                }
                R.id.appointments -> {
                    startActivity(Intent(this, PatientDashboard::class.java))
                }*/


                R.id.profile -> {
                    startActivity(Intent(this, DoctorProfile::class.java))
                }
                R.id.logout -> {
                    Toast.makeText(applicationContext, "Logging out!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            true
        }
    }
}
