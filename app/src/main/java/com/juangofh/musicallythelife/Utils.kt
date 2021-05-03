package com.juangofh.musicallythelife

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever

fun createAlbumArt(mmr: MediaMetadataRetriever): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val embedPic = mmr.embeddedPicture
        bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic!!.size)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            mmr.release()
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
    return bitmap
}

fun millisecondsToString(time: Int): String {
    val minutes: Int = time / 1000 / 60
    val seconds: Int = time / 1000 % 60
    if (seconds < 10)
        return "$minutes:0$seconds"
    return "$minutes:$seconds"
}

fun isColorDark(color: Int): Boolean {
    val darkness = 1 - (0.299 * Color.red(color) +
            0.587 * Color.green(color) +
            0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}