package com.example.ch08_simplemusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicPlayerService: Service() {

    private var mMediaPlayer: MediaPlayer? = null

    private var mBinder: MusicPlayerBinder = MusicPlayerBinder()

    inner class MusicPlayerBinder: Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }
    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mChannel = NotificationChannel(
            "CHANNEL_ID", "CHANNEL_NAME",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(mChannel)

        val notification: Notification = Notification.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle("뮤직 플레이어 앱")
            .setContentText("앱이 실행 중입니다.")
            .build()

        startForeground(1, notification)
    }

    fun isPlaying() : Boolean {
        return (mMediaPlayer != null && mMediaPlayer?.isPlaying ?: false)
    }

    fun play() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.test)
            mMediaPlayer?.isLooping = true
            mMediaPlayer?.setVolume(1.0F, 1.0F)
        }
        mMediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun pause() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun stop() {
        mMediaPlayer?.let {
            it.stop()
            it.release()
            mMediaPlayer = null
        }
    }
}