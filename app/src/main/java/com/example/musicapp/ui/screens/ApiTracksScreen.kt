package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicapp.data.TrackList
import com.example.musicapp.ui.viewmodels.ApiTracksViewModel

@Composable
fun ApiTracksScreen(
    viewModel: ApiTracksViewModel = viewModel(),
    onTrackClick: (ApiTrack) -> Unit
) {
    val context = LocalContext.current
    val tracks = viewModel.tracks
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

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
                modifier = Modifier.padding(16.dp)
        } else {
            TrackList(tracks = tracks, onTrackClick = onTrackClick)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTopTracks()
    }
}