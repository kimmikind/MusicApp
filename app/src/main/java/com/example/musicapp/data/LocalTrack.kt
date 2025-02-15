package com.example.musicapp.data

data class LocalTrack (
    val id: Long,
    val title: String,
    val artist: String,
    val coverUrl: String?,
    val filePath: String,
    val duration: Long // in milliseconds
)
