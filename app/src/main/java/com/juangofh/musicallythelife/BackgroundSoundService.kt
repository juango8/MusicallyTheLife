package com.juangofh.musicallythelife

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class BackgroundSoundService : Service() {

    private var player: MediaPlayer? = null
    private var listener: OnProgressListener? = null
    private val binder = BackgroundSoundBinder()

    inner class BackgroundSoundBinder : Binder() {
        fun getService(): BackgroundSoundService = this@BackgroundSoundService
    }

    override fun onBind(arg0: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this, R.raw.all_my_love)
        player?.isLooping = true
        player?.setVolume(100f, 100f)
    }

    fun setOnProgressListener(listener: OnProgressListener) {
        this.listener = listener
    }

    fun startMusic() {
        val currentTime = Preferences.getMediaPosition(applicationContext)
        if (currentTime > 0 && currentTime != player?.duration) {
            player?.start()
            player?.seekTo(currentTime)
            listener?.currentProgress(currentTime, player?.duration ?: 0)
        } else {
            player?.start()
            listener?.currentProgress(0, player?.duration ?: 0)
        }
        listener?.onPlaySong()
    }

    fun pauseMusic() {
        val currentPosition = player?.currentPosition ?: 0
        Preferences.setMediaPosition(applicationContext, currentPosition)
        player?.pause()
        listener?.onPauseSong()
    }

    fun seekMusic(position: Int) {
        player?.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    fun duration(): Int {
        return player?.duration ?: 0
    }

    fun currentTime(): Int {
        return player?.currentPosition ?: 0
    }

}
