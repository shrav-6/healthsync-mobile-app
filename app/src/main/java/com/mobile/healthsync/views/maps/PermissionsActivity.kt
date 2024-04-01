package com.mobile.healthsync.views.maps

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mobile.healthsync.BaseActivity
import com.mobile.healthsync.R
import java.util.function.Consumer

/**
 * Activity responsible for handling permissions related to accessing device location.
 */
class PermissionsActivity : BaseActivity() {

    // UI components
    private lateinit var btnGrant: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        // Check if ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, proceed to MapActivity
            startActivity(Intent(this@PermissionsActivity, MapActivity::class.java))
            finish() // Finish the current activity
            return
        }

        // Initialize grant button
        btnGrant = findViewById(R.id.btn_grant)

        // Set click listener for the grant button
        btnGrant.setOnClickListener {
            // Request permission using Dexter library
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        // Permission granted, start MapActivity
                        startActivity(Intent(this@PermissionsActivity, MapActivity::class.java))
                        finish() // Finish the current activity
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if (response?.isPermanentlyDenied == true) {
                            // Permission permanently denied, prompt user to go to settings to enable it
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(this@PermissionsActivity)
                            builder.setTitle("Permission Denied")
                                .setMessage("Permission to access device location is permanently denied. You need to go to settings to allow the permission.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        // Open app settings
                                        val intent = Intent()
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        intent.setData(Uri.fromParts("package", packageName, null))
                                    }
                                })
                                .show()
                        } else {
                            // Permission denied, show a toast
                            Toast.makeText(
                                this@PermissionsActivity,
                                "Permission Denied",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        // Continue with the permission request
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    override fun onPerformDirectAction(
        actionId: String,
        arguments: Bundle,
        cancellationSignal: CancellationSignal,
        resultListener: Consumer<Bundle>
    ) {
        super.onPerformDirectAction(actionId, arguments, cancellationSignal, resultListener)
    }
}
