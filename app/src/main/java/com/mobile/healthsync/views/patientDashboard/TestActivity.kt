package com.mobile.healthsync.views.patientDashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mobile.healthsync.R
import java.util.Calendar

class TestActivity : AppCompatActivity() {

    private val SUCCESS: Int = 1
    private val FAILURE:  Int = 0

    private var bookingSlot : Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        var cancellpay = findViewById<Button>(R.id.paymentfailure)
        var donepay = findViewById<Button>(R.id.paymentsuccess)

        donepay.setOnClickListener{
            val returnIntent = Intent()
            returnIntent.putExtra("status", "Operation Success")
            returnIntent.putExtra("payment_id", 1)
            setResult(this.SUCCESS, returnIntent)
            finish()
        }

        cancellpay.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("status", "Operation failure")
            setResult(this.FAILURE, returnIntent)
            finish()
        }

    }
}