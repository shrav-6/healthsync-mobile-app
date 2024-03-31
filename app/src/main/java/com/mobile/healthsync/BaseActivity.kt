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
import com.mobile.healthsync.views.events.EventsActivity
import com.mobile.healthsync.views.maps.PermissionsActivity
import com.mobile.healthsync.views.patientDashboard.PatientAppointmentListActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.patientDashboard.PatientInsights
import com.mobile.healthsync.views.patientDashboard.PatientToDo
import com.mobile.healthsync.views.patientProfile.PatientProfile
import com.mobile.healthsync.views.signUp.SignupActivity

open class BaseActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_toolbar, null) as DrawerLayout
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.activity_content)
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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
        val drawerLayout = findViewById<DrawerLayout>(R.id.activity_container)
        val navView = findViewById<NavigationView>(R.id.navigationView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.dashboard -> {
                    startActivity(Intent(this, PatientDashboard::class.java))
                }
                R.id.pharmacies -> {
                    startActivity(Intent(this, PermissionsActivity::class.java))
                }
                R.id.appointments -> {
                    startActivity(Intent(this, PatientAppointmentListActivity::class.java))
                }
                R.id.TODO -> {
                    startActivity(Intent(this, PatientToDo::class.java))
                }
                R.id.RSVP -> {
                    startActivity(Intent(this, EventsActivity::class.java))
                }
                R.id.Insights -> {
                    startActivity(Intent(this, PatientInsights::class.java))
                }
                R.id.profile -> {
                    startActivity(Intent(this, PatientProfile::class.java))
                }
                R.id.logout -> {
                    Toast.makeText(applicationContext, "Logging out!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, SignupActivity::class.java)//change to login activity later
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
}
