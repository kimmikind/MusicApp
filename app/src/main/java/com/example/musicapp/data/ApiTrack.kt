package com.example.musicapp.data

data class TopTracksResponse(
    val tracks: TrackList
)

data class TrackList(
    val data: List<Track>
)

data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String
)

data class Artist(
    val name: String
)

data class Album(
    val cover_medium: String
)

data class SearchTracksResponse(
    val data: List<Track>
)