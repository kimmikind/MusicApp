package com.example.musicapp.data.local

data class LocalTrack (
    val id: Long,
    val title: String,
    val artist: String,
    val coverPath: String?,
    val filePath: String
)
