package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.musicapp.data.Track
import com.example.musicapp.ui.items.SearchBar
import com.example.musicapp.ui.items.TrackList1
import com.example.musicapp.ui.viewmodels.ApiTracksViewModel

@Composable
fun ApiTracksScreen(
    viewModel: ApiTracksViewModel,
    onTrackClick: (Track) -> Unit
) {
    val context = LocalContext.current
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(onSearch = { query ->
            if (query.isNotEmpty()) {
                viewModel.searchTracks(query)
            } else {
                viewModel.loadTopTracks()
            }
        })

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text(
                text = "Error: $error",
                color = Color.Red,
                modifier = Modifier.padding(16.dp))
        } else {
            TrackList1(tracks = tracks, onTrackClick = onTrackClick)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTopTracks()
    }
}