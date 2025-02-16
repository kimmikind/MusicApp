package com.example.musicapp.ui.items

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.musicapp.data.LocalTrack
import com.example.musicapp.data.Track

@Composable
fun TrackList(tracks: List<LocalTrack>, onTrackClick: (LocalTrack) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackItem(track = track, onClick = { onTrackClick(track) })
        }
    }
}

@Composable
fun TrackList1(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tracks) { track ->
            TrackItem1(track = track, onClick = { onTrackClick(track) })
        }
    }
}