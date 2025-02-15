package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.musicapp.data.local.LocalTrack
import com.example.musicapp.ui.items.SearchBar
import com.example.musicapp.ui.items.TrackList
import com.example.musicapp.ui.viewmodels.LocalTrackViewModel

@Composable
fun LocalTracksScreen(
    viewModel: LocalTrackViewModel, onTrackClick: (LocalTrack) -> Unit
) {
    val tracks by viewModel.filteredTracks.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        // Поисковая строка
        SearchBar(onSearch = { query -> viewModel.searchTracks(query) })

        // Список треков
        TrackList(tracks = tracks, onTrackClick = onTrackClick)
    }
}