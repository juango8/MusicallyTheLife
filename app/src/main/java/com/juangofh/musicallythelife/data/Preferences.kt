package com.juangofh.musicallythelife.data

import android.content.Context

object Preferences {

    private const val PREFS = "Sound2Preferences"

    fun setMediaPosition(context: Context, position: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt("position", position).apply()
    }

    fun getMediaPosition(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt("position", 0)
    }

}