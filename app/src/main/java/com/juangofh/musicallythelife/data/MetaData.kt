package com.juangofh.musicallythelife.data

data class MetaData(
    var nameOfSong: String,
    var author: String,
    var album: String,
    var genre: String,
    var year: String,
    var albumCover: String = ""
)