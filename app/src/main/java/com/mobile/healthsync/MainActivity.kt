//package com.mobile.healthsync
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import androidx.activity.ComponentActivity
//import com.mobile.healthsync.views.signUp.SignupActivity
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val handler = Handler()
//        handler.postDelayed({
//            val intent = Intent(
//                this@MainActivity,
//                SignupActivity::class.java
//            )
//            startActivity(intent)
//        }, 3000)
//    }
//}
package com.mobile.healthsync
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.integerArrayResource
import com.mobile.healthsync.model.Doctor
import com.mobile.healthsync.views.patientDashboard.DoctorInfoActivity
import com.mobile.healthsync.views.patientDashboard.PatientDashboard
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, DoctorInfoActivity::class.java)
        intent.putExtra("doctor_id",663)
        intent.putExtra("patient_id",251)
        startActivity(intent)
    }
}