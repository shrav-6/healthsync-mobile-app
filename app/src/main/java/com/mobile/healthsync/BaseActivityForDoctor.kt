package com.mobile.healthsync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mobile.healthsync.repository.DoctorRepository
import com.mobile.healthsync.views.doctorDashboard.DoctorDashboard
import com.mobile.healthsync.views.doctorDashboard.DoctorReviewsActivity
import com.mobile.healthsync.views.doctorProfile.DoctorProfile
import com.mobile.healthsync.views.login.LoginActivity
import com.squareup.picasso.Picasso

open class BaseActivityForDoctor : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var doctorRepository: DoctorRepository

    override fun setContentView(@LayoutRes layoutResID: Int) {
        val sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)


        val fullView = layoutInflater.inflate(R.layout.activity_toolbar_doctor, null) as DrawerLayout
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.activity_content1)
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)

        //val navigationView = findViewById<EditText>(R.id.navigationView)
        // Find the TextViews in the navigation header
        val navigationHeaderView = findViewById<NavigationView>(R.id.navigationView1).getHeaderView(0)
        val nameOfDoctorTextView = navigationHeaderView.findViewById<TextView>(R.id.name_of_doctor)
        val doctorIdTextView = navigationHeaderView.findViewById<TextView>(R.id.doctorId)
        val photo = navigationHeaderView.findViewById<ImageView>(R.id.profileImage)

        //nameOfPatientTextView.text = "Patient Name"
        nameOfDoctorTextView.text = sharedPreferences.getString("doctor_name","Doctor Name").toString()
        doctorIdTextView.text = sharedPreferences.getString("doctor_id","123").toString()

        doctorRepository = DoctorRepository(this)

        doctorRepository.getPhotoForDoctor(sharedPreferences.getString("doctor_documentid", "OXyUFwt5a5S9yUmclEd3").toString()) {
            if (it == "null") {
                photo.setImageResource(R.drawable.user)
            } else {
                Picasso.get().load(Uri.parse(it)).into(photo)
            }
        }

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
                R.id.dashboard -> {
                    startActivity(Intent(this, DoctorDashboard::class.java))
                }
                R.id.reviews -> {
                    startActivity(Intent(this, DoctorReviewsActivity::class.java))
                }
                R.id.profile -> {
                    startActivity(Intent(this, DoctorProfile::class.java))
                }
                R.id.logout -> {
                    Toast.makeText(applicationContext, "Logging out!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, LoginActivity::class.java)//change to login activity later
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
}
