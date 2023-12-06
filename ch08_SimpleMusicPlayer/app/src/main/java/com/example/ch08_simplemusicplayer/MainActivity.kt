package com.example.ch08_simplemusicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.ch08_simplemusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mService: MusicPlayerService? = null

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as MusicPlayerService.MusicPlayerBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener { mService?.play() }
        binding.btnPause.setOnClickListener { mService?.pause() }
        binding.btnStop.setOnClickListener { mService?.stop() }
    }

    override fun onResume() {
        super.onResume()

        if (mService == null) {
            startForegroundService(Intent(this, MusicPlayerService::class.java))

            val intent = Intent(this, MusicPlayerService::class.java)
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()

        if (mService != null) {
            if (!mService!!.isPlaying()) {
                mService!!.stopSelf()
            }
            unbindService(mServiceConnection)
            mService = null
        }
    }
}