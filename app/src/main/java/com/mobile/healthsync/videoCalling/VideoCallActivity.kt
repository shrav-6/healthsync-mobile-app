package com.mobile.healthsync.videoCalling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.healthsync.R
import java.util.*
import android.view.View
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.Manifest
import android.content.pm.PackageManager
import android.view.SurfaceView
import android.widget.FrameLayout

class VideoCallActivity : AppCompatActivity() {

    private lateinit var mRtcEngine: RtcEngine
    private var mIsInCall = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // Initialize Agora RTC Engine
        initializeAgoraEngine()

        // Setup UI components
        setupUI()
    }

    private fun initializeAgoraEngine() {
        val appID = "YOUR_AGORA_APP_ID"
        try {
            mRtcEngine = RtcEngine.create(baseContext, appID, mRtcEventHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupUI() {
        // Setup buttons
        findViewById<View>(R.id.start_end_call_button).setOnClickListener {
            toggleCall()
        }

        findViewById<View>(R.id.switch_camera_button).setOnClickListener {
            switchCamera()
        }

        findViewById<View>(R.id.audio_toggle_button).setOnClickListener {
            toggleAudio()
        }
    }

    private fun toggleCall() {
        if (mIsInCall) {
            mRtcEngine.leaveChannel()
            mIsInCall = false
        } else {
            val channelName = intent.getStringExtra("CHANNEL_NAME")
            mRtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0)
            mIsInCall = true
        }
    }

    private fun switchCamera() {
        mRtcEngine.switchCamera()
    }

    private fun toggleAudio() {
        // Assuming isMuted is a boolean indicating whether audio is currently muted or not
        val isMuted = true // Set to the appropriate value
        mRtcEngine.muteLocalAudioStream(isMuted)
    }

    private val mRtcEventHandler = object : io.agora.rtc.IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            // Handle join channel success event
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            // Handle first remote video decoded event
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Handle user offline event
        }
    }
}
