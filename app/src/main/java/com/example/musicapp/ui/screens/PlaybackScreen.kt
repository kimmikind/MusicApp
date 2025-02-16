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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.ui.viewmodels.PlaybackViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.musicapp.data.ApiTrack
import com.example.musicapp.data.Track
import com.example.musicapp.data.api.DeezerApi
import com.example.musicapp.ui.viewmodels.ApiTracksViewModel
import com.example.musicapp.ui.viewmodels.LocalTrackViewModel
import com.example.musicapp.ui.viewmodels.PlaybackViewModel.PlaybackState


@Composable
fun PlaybackScreen(trackId: Long?, source: String?,
                   localTrackViewModel: LocalTrackViewModel,
                   playbackViewModel: PlaybackViewModel,
                   apiTrackViewModel : ApiTracksViewModel) {


    // Получаем текущий трек из PlaybackViewModel
    val currentTrack by playbackViewModel.currentTrack.collectAsState()

    // Загружаем трек при изменении trackId и source
    LaunchedEffect(key1 = trackId, key2 = source) {
        if (trackId != null) {
        when (source) {
            "local" -> trackId.let {
                Log.d("PlaybackScreen", "Loading local track with id: $trackId")
                localTrackViewModel.getTrackById(it) }
            "api" -> {
                trackId.let {
                    Log.d("PlaybackScreen", "Loading API track with id: $trackId")
                    // Загружаем трек из API
                    apiTrackViewModel.loadTrackById(it)
                }

            }

        }
            playbackViewModel.pause()
        }

    }

    // Получаем трек из StateFlow для локальных данных
    val localTrack by localTrackViewModel.currentTrack.collectAsState()
    val apiTrack by apiTrackViewModel.currentTrack.collectAsState()

    // Устанавливаем трек в PlaybackViewModel в зависимости от источника
    LaunchedEffect(key1 = localTrack, key2 = apiTrack) {
        Log.d("PlaybackScreen", "LaunchedEffect сработал: localTrack = $localTrack, apiTrack = $apiTrack")
        when (source) {

            "local" -> {

                localTrack?.let {
                    Log.d("PlaybackScreen", "Устанавливаем локальный трек в PlaybackViewModel: $it")
                    playbackViewModel.setLocalTrack(it)
                }
            }
            "api" -> {
                apiTrack?.let {
                    Log.d("PlaybackScreen", "Устанавливаем api трек в PlaybackViewModel: $it")
                    playbackViewModel.setApiTrack(it)
                }
            }
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
        val coverUrl = currentTrack?.coverUrl // Получаем URL картинки

        if (coverUrl != null) {
            if (coverUrl.startsWith("file://")) {
                // Если URL это локальный путь
                val bitmap = BitmapFactory.decodeFile(coverUrl.removePrefix("file://"))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Album Cover",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Если это URL с API
                Image(
                    painter = rememberAsyncImagePainter(coverUrl),
                    contentDescription = "Album Cover",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            // Если нет URL, используем изображение-заполнитель
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Placeholder",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Название трека и исполнитель
        Text(text = currentTrack?.title ?: "Unknown Title", style = MaterialTheme.typography.headlineSmall)
        Text(text = currentTrack?.artist ?: "Unknown Artist", style = MaterialTheme.typography.bodyMedium)

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
