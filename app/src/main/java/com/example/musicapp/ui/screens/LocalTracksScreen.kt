package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.musicapp.data.LocalTrack
import com.example.musicapp.ui.items.SearchBar
import com.example.musicapp.ui.items.TrackList
import com.example.musicapp.ui.viewmodels.LocalTrackViewModel

@Composable
fun LocalTracksScreen(
    viewModel: LocalTrackViewModel, onTrackClick: (LocalTrack) -> Unit
) {

   // Вызываем loadTracks() один раз при первом запуске
    LaunchedEffect(Unit) {
        viewModel.loadTracks()
    }

    val tracks by viewModel.filteredTracks.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        // Поисковая строка
        SearchBar(onSearch = { query -> viewModel.searchTracks(query) })
        // Список треков
        TrackList(tracks = tracks, onTrackClick = onTrackClick)
    }
}