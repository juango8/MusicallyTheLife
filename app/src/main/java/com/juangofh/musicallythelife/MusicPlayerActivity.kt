package com.juangofh.musicallythelife

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import kotlinx.android.synthetic.main.activity_music_player.*

class MusicPlayerActivity : AppCompatActivity(), OnProgressListener {

    private var mBound: Boolean = false
    private lateinit var mService: BackgroundSoundService

    @Suppress("DEPRECATION")
    private val mHandler = Handler()
    private var duration: Int = 0
    private lateinit var context: Context

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundSoundService.BackgroundSoundBinder
            mService = binder.getService()
            mBound = true
            if (mService.isPlaying()) {
                duration = mService.duration()
                player_seekbar.max = duration
                mRunnable.run()
                onPlaySong()
            }
            Toast.makeText(this@MusicPlayerActivity, "connected", Toast.LENGTH_SHORT).show()
            val metaData = mService.getMetadata(context)
            text_title.text = metaData.nameOfSong
            text_artist.text = metaData.author
            image_album_art.setImageURI(Uri.parse(metaData.albumCover))
            setBackgroundColors(context, metaData.albumCover)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Toast.makeText(this@MusicPlayerActivity, "disconnected", Toast.LENGTH_SHORT).show()
            mBound = false
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
        startService(Intent(this, BackgroundSoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        })
        button_play.setOnClickListener {
            if (mBound) {
                mService.setOnProgressListener(this)
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
    }

    override fun onPlaySong() {
        button_play.setImageResource(R.drawable.ic_pause)
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
//                val textColor = if (isColorDark(backgroundColor)) Color.WHITE else Color.BLACK
//                text_title.setTextColor(textColor)
//                text_artist.setTextColor(textColor)
            }
        }
    }
}