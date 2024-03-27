package com.mobile.healthsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
import com.mobile.healthsync.views.patientProfile.PatientProfile

open class BaseActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerlayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerlayout, R.string.open, R.string.close)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId){
                R.id.dashboard -> {
                    val intent = Intent(this,PatientDashboard::class.java)
                    startActivity(intent)
                }
                /*R.id.appointments -> {
                    val intent = Intent(this,CitiesActivity::class.java)
                    startActivity(intent)
                }
                R.id.pharmacies-> {
                    val intent = Intent(this,CitiesActivity::class.java)
                    startActivity(intent)
                }*/
                R.id.profile-> {
                    val intent = Intent(this,PatientProfile::class.java)
                    startActivity(intent)
                }

                R.id.logout -> {
                    Toast.makeText(applicationContext,"Logging out!",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}