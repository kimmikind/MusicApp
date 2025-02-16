package com.example.musicapp.ui.viewmodels

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.media.session.PlaybackState
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.ApiTrack
import com.example.musicapp.data.LocalTrack
import com.example.musicapp.data.Track

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.delay


class PlaybackViewModel(
    private val localTrackViewModel: LocalTrackViewModel,
    private val apiTracksViewModel: ApiTracksViewModel,
) : ViewModel() {



    private val mediaPlayer = MediaPlayer()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Paused)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _trackDuration = MutableStateFlow(0L)
    val trackDuration: StateFlow<Long> = _trackDuration
    // Список треков и индекс текущего трека
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    private var currentTrackIndex = 0

    init {
        // Слушаем изменения в локальных треках
        viewModelScope.launch {
            localTrackViewModel.tracks.collect { tracks ->
                _tracks.value = tracks.map { it.toTrack() }
                Log.d("PlaybackViewModel", "Список локальных треков обновлен: ${tracks.size} треков")
            }
        }

        // Слушаем изменения в треках из API
        viewModelScope.launch {
            apiTracksViewModel.tracks.collect { tracks ->
                _tracks.value = tracks
                Log.d("PlaybackViewModel", "Список API треков обновлен: ${tracks.size} треков")
            }
        }

        // Обновляем текущую позицию воспроизведения
        viewModelScope.launch {
            while (true) {
                if (mediaPlayer.isPlaying) {
                    _currentPosition.value = mediaPlayer.currentPosition.toLong()
                }
                delay(1000) // Обновляем каждую секунду
            }
        }


    }

    // Устанавливаем трек, если он из локальных данных
    fun setLocalTrack(track: LocalTrack) {
        val newTrack = track.toTrack()
        _currentTrack.value = newTrack
        prepareMediaPlayer(newTrack.previewUrl)
    }

    // Устанавливаем трек, если он из API
    fun setApiTrack(track: Track) {
        Log.d("PlaybackViewModel", "Setting API track: $track")
        //val newTrack = track.toTrack()
        _currentTrack.value = track
        Log.d("PlaybackViewModel", "Current track after setting: ${_currentTrack.value}")
        Log.e("RRR","$track")
        prepareMediaPlayer(track.previewUrl)
    }

    private fun prepareMediaPlayer(url: String) {
        Log.d("PlaybackViewModel", "Preparing MediaPlayer with URL: $url")

            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                Log.d("PlaybackViewModel", "MediaPlayer prepared, duration: ${it.duration}")
                _trackDuration.value = it.duration.toLong()
                play()
            }


    }

    fun play() {
        mediaPlayer.start()
        _playbackState.value = PlaybackState.Playing
    }

    fun pause() {
        mediaPlayer.pause()
        _playbackState.value = PlaybackState.Paused
    }

    fun seekTo(position: Long) {
        mediaPlayer.seekTo(position.toInt())
        _currentPosition.value = position
    }

    fun nextTrack() {
        // Логика переключения на следующий трек (локальный или из API)
        if (_tracks.value.isNotEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % _tracks.value.size
            _currentTrack.value = _tracks.value[currentTrackIndex]
            prepareMediaPlayer(_tracks.value[currentTrackIndex].previewUrl)
        }
    }

    fun previousTrack() {
        if (_tracks.value.isNotEmpty()) {
            currentTrackIndex = (currentTrackIndex - 1 + _tracks.value.size) % _tracks.value.size
            _currentTrack.value = _tracks.value[currentTrackIndex]
            prepareMediaPlayer(_tracks.value[currentTrackIndex].previewUrl)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

    sealed class PlaybackState {
        object Playing : PlaybackState()
        object Paused : PlaybackState()
    }
    fun LocalTrack.toTrack() = Track(
        id = id,
        title = title,
        artist = artist,
        coverUrl = coverUrl,
        previewUrl = filePath
    )



}



