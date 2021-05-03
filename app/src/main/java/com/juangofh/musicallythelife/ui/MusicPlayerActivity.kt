package com.juangofh.musicallythelife.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.juangofh.musicallythelife.*
import com.juangofh.musicallythelife.ApplicationClass.Companion.ACTION_NEXT
import com.juangofh.musicallythelife.ApplicationClass.Companion.ACTION_PLAY
import com.juangofh.musicallythelife.ApplicationClass.Companion.ACTION_PREV
import com.juangofh.musicallythelife.ApplicationClass.Companion.CHANNEL_ID_2
import com.juangofh.musicallythelife.data.MetaData
import com.juangofh.musicallythelife.data.NotificationReceiver
import com.juangofh.musicallythelife.service.BackgroundSoundService
import com.juangofh.musicallythelife.service.OnProgressListener
import com.juangofh.musicallythelife.utils.millisecondsToString
import kotlinx.android.synthetic.main.activity_music_player.*

class MusicPlayerActivity : AppCompatActivity(), OnProgressListener {

    private lateinit var metaData: MetaData
    private var mBound: Boolean = false
    private lateinit var mService: BackgroundSoundService

    @Suppress("DEPRECATION")
    private val mHandler = Handler()
    private var duration: Int = 0
    private lateinit var context: Context
    private lateinit var mediaSession: MediaSessionCompat

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundSoundService.BackgroundSoundBinder
            mService = binder.getService()
            mBound = true
            mService.setOnProgressListener(this@MusicPlayerActivity)
            metaData = mService.getMetadata(context)
            text_title.text = metaData.nameOfSong
            text_artist.text = metaData.author
            image_album_art.setImageURI(Uri.parse(metaData.albumCover))
            setBackgroundColors(context, metaData.albumCover)
            if (mService.isPlaying()) {
                duration = mService.duration()
                player_seekbar.max = duration
                mRunnable.run()
                onPlaySong()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false // TODO mService = null?
        }

    }
    private val mRunnable: Runnable = object : Runnable {

        override fun run() {
            val currentTime: Int = mService.currentTime()
            player_seekbar.setProgress(currentTime, true)
            text_current_time.text = millisecondsToString(currentTime)
            mHandler.postDelayed(this, 1000)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        context = this@MusicPlayerActivity
        mediaSession = MediaSessionCompat(this, "PlayerAudio")
        startService(Intent(this, BackgroundSoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        })
        button_play.setOnClickListener {
            if (mBound) {
                if (mService.isPlaying()) {
                    mService.pauseMusic()
                } else {
                    mService.startMusic()
                }
            }
        }
        back_button.setOnClickListener { onBackPressed() }
        player_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mService.seekMusic(progress)
                    seekBar?.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    override fun onStart() {
        super.onStart()
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(connection)
        mBound = false
    }

    override fun currentProgress(time: Int, duration: Int) {
        player_seekbar.max = duration
        this.duration = duration
        text_total_time.text = millisecondsToString(duration)
        player_seekbar.setProgress(time, true)
        mRunnable.run()
    }

    override fun onPauseSong() {
        button_play.setImageResource(R.drawable.ic_play_arrow)
        if (mBound)
            showNotification(R.drawable.ic_play_arrow)
    }

    override fun onPlaySong() {
        button_play.setImageResource(R.drawable.ic_pause)
        if (mBound)
            showNotification(R.drawable.ic_pause)
    }

    fun setBackgroundColors(context: Context, pathImageResource: String) {
        val image = BitmapFactory.decodeFile(pathImageResource)
        Palette.from(image).generate { palette ->
            palette?.let {
                val backgroundColor = it.getDominantColor(
                    ContextCompat.getColor(
                        context,
                        R.color.brown
                    )
                )
                view1.setBackgroundColor(backgroundColor)
                view2.setBackgroundColor(backgroundColor)
            }
        }
    }

    private fun showNotification(playPauseBtn: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val contentIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PREV)
        val prevPendingIntent =
            PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val playIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val picture: Bitmap? = BitmapFactory.decodeFile(metaData.albumCover)
//            BitmapFactory.decodeResource(resources, metaData.albumCover)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_2)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(picture)
            .setContentTitle(metaData.nameOfSong)
            .setContentText(metaData.author)
            .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}