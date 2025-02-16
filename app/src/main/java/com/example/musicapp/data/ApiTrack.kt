package com.example.musicapp.data


data class ApiTrack(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String
)

data class TopTracksResponse(
    val tracks: TrackList
)

data class TrackList(
    val data: List<ApiTrack>
)

// Основная модель для работы внутри приложения
data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val coverUrl: String?,
    val previewUrl: String
)

data class Artist(
    val name: String
)

data class Album(
    val cover_medium: String
)

data class SearchTracksResponse(
    val data: List<ApiTrack>
)