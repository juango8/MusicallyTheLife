package com.juangofh.musicallythelife.service

interface OnProgressListener {

    fun currentProgress(time: Int, duration: Int)
    fun onPauseSong()
    fun onPlaySong()

}