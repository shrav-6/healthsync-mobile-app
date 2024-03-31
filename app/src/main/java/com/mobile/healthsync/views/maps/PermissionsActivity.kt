package com.mobile.healthsync.views.maps

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import com.mobile.healthsync.views.TestActivity
import java.util.function.Consumer

class PermissionsActivity : BaseActivity() {

    private lateinit var btnGrant : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startActivity(Intent(this@PermissionsActivity,MapActivity::class.java))
            finish()
            return
        }

        btnGrant = findViewById(R.id.btn_grant)

        btnGrant.setOnClickListener{
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener( object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        startActivity(Intent(this@PermissionsActivity, MapActivity::class.java))
                        finish()

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if(response?.isPermanentlyDenied == true)
                        {
                            val builder : AlertDialog.Builder =AlertDialog.Builder(this@PermissionsActivity)
                            builder.setTitle("Permission Denied")
                                .setMessage("Permission to access device location is permanentaly denied. you need to go to settings to allow the permission.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        val intent = Intent()
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        intent.setData(Uri.fromParts("package",packageName, null))
                                    }
                                })
                                .show()
                        }
                        else {
                            Toast.makeText(this@PermissionsActivity,"Permission Denied",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
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