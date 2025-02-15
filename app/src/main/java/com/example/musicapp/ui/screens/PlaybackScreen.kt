package com.example.musicapp.ui.screens


import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.ui.viewmodels.LocalTrackViewModel
import com.example.musicapp.ui.viewmodels.PlaybackViewModel.PlaybackState


@Composable
fun PlaybackScreen(trackId: Long?, source: String?,
                   localTrackViewModel: LocalTrackViewModel) {
    val playbackViewModel: PlaybackViewModel = viewModel()
    //val localTrackViewModel: LocalTrackViewModel = viewModel()

    // Получаем трек из StateFlow
    val track by localTrackViewModel.currentTrack.collectAsState()

    // Загружаем трек при изменении trackId и source
    LaunchedEffect(key1 = trackId, key2 = source) {
        if (trackId != null) {
        when (source) {
            "local" -> trackId.let { localTrackViewModel.getTrackById(it) }
            "api" -> {
                // apiViewModel.getTrackById(trackId) // если есть API
            }

        }
            playbackViewModel.pause()
        }

    }

    val playbackState by playbackViewModel.playbackState.collectAsState()
    val currentPosition by playbackViewModel.currentPosition.collectAsState()
    val trackDuration by playbackViewModel.trackDuration.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Обложка трека
        track?.coverUrl?.let { coverUrl ->
            val bitmap = BitmapFactory.decodeFile(coverUrl)
            Image(
                bitmap = bitmap.asImageBitmap(),
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

        // Название трека и исполнитель
        Text(text = track?.title ?: "Unknown Title", style = MaterialTheme.typography.headlineSmall)
        Text(text = track?.artist ?: "Unknown Artist", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { playbackViewModel.seekTo(it.toLong()) },
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
            IconButton(onClick = { playbackViewModel.previousTrack() }) {
                Icon(painter = painterResource(id = R.drawable.ic_previous), contentDescription = "Previous")
            }

            IconButton(onClick = {
                when (playbackState) {
                    PlaybackState.Playing -> playbackViewModel.pause()
                    PlaybackState.Paused -> playbackViewModel.play()
                }
            }) {
                Icon(
                    painter = painterResource(
                        id = if (playbackState == PlaybackState.Playing) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = "Play/Pause"
                )
            }

            IconButton(onClick = { playbackViewModel.nextTrack() }) {
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
