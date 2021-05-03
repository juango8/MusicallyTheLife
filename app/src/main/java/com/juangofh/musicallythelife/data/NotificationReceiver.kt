package com.juangofh.musicallythelife.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.juangofh.musicallythelife.service.BackgroundSoundService

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PREV = "PREVIOUS"
        const val ACTION_PLAY = "PLAY"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val mIntent = Intent(context, BackgroundSoundService::class.java)
        when (intent?.action) {
            ACTION_PLAY -> {
                mIntent.putExtra("myActionName", intent.action)
                context?.startService(mIntent)
            }
            ACTION_NEXT -> {
                mIntent.putExtra("myActionName", intent.action)
                context?.startService(mIntent)
            }
            ACTION_PREV -> {
                mIntent.putExtra("myActionName", intent.action)
                context?.startService(mIntent)
            }
        }
    }
}
