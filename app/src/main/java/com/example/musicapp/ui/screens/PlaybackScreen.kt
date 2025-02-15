package com.example.musicapp.ui.screens

import android.media.session.PlaybackState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.ui.viewmodels.PlaybackViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

@Composable
fun PlaybackScreen(trackId: Long?, source: String?) {
    val viewModel: PlaybackViewModel = viewModel()
    val context = LocalContext.current

    // в зависимости от источника
    val track = remember(trackId, source) {
        when (source) {
            "local" -> {

                // Например: repository.getLocalTrackById(trackId)
            }
            "api" -> {

                // Например: apiService.getTrackById(trackId)
            }
            else -> null
        }
    }

    val playbackState by viewModel.playbackState.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val trackDuration by viewModel.trackDuration.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // обложка
        track?.coverUrl?.let { coverUrl ->
            Image(
                bitmap = /* Load image from URL */,
                contentDescription = "Album Cover",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = "Placeholder",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // название трека и исполнитель
        Text(text = track?.title ?: "Unknown Title", style = MaterialTheme.typography.headlineSmall)
        Text(text = track?.artist ?: "Unknown Artist", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..trackDuration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // Текущая позиция и продолжительность
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(currentPosition))
            Text(text = formatTime(trackDuration))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Элементы управления воспроизведением
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.previousTrack() }) {
                Icon(painter = painterResource(id = R.drawable.ic_previous), contentDescription = "Previous")
            }

            IconButton(onClick = {
                when (playbackState) {
                    is PlaybackState.Playing -> viewModel.pause()
                    is PlaybackState.Paused -> viewModel.play()
                }
            }) {
                Icon(
                    painter = painterResource(
                        id = if (playbackState is PlaybackState.Playing) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = "Play/Pause"
                )
            }

            IconButton(onClick = { viewModel.nextTrack() }) {
                Icon(painter = painterResource(id = R.drawable.ic_next), contentDescription = "Next")
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}