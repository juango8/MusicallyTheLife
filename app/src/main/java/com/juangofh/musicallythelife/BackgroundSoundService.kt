package com.juangofh.musicallythelife

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.io.FileOutputStream

class BackgroundSoundService : Service() {

    private var player: MediaPlayer? = null
    private var listener: OnProgressListener? = null
    private val binder = BackgroundSoundBinder()
    private var currentSong: Int = R.raw.all_my_love

    inner class BackgroundSoundBinder : Binder() {
        fun getService(): BackgroundSoundService = this@BackgroundSoundService
    }

    override fun onBind(arg0: Intent?): IBinder {
        return binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Toast.makeText(this, "rebind", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra("myActionName")) {
            NotificationReceiver.ACTION_PLAY -> {
                Toast.makeText(this, "play", Toast.LENGTH_SHORT).show()
                if (player?.isPlaying == true) {
                    pauseMusic()
                    listener?.onPauseSong()
                } else {
                    startMusic()
                    listener?.onPlaySong()
                }
            }
            NotificationReceiver.ACTION_NEXT -> {
                Toast.makeText(this, "next", Toast.LENGTH_SHORT).show()
//                listener?.nextClicked()
            }
            NotificationReceiver.ACTION_PREV -> {
                Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show()
//                listener?.prevClicked()
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this, currentSong)
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

    fun getMetadata(context: Context): MetaData {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(
            context,
            Uri.parse("android.resource://com.juangofh.musicallythelife/$currentSong")
        )

        val nameOfSong = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "unknown"
        val authorOfSong =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "unknown"
        val album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "unknown"
        val genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: "unknown"
        val year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: "unknown"

        val destFolder = cacheDir.absolutePath
        val out = FileOutputStream("$destFolder/myAlbumArt.png")
        createAlbumArt(mmr)?.compress(Bitmap.CompressFormat.PNG, 100, out)

        return MetaData(nameOfSong, authorOfSong, album, genre, year, "$destFolder/myAlbumArt.png")
    }

}
