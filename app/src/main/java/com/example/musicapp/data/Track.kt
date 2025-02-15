package com.example.musicapp.data

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val coverUrl: String?,
    val duration: Long // in milliseconds
)