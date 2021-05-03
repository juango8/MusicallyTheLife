package com.juangofh.musicallythelife

interface OnProgressListener {

    fun currentProgress(time: Int, duration: Int)
    fun onPauseSong()
    fun onPlaySong()

}