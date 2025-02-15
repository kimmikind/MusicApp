package com.example.musicapp.ui.viewmodels

import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.LocalTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.musicapp.data.repository.LocalTRepository
import kotlinx.coroutines.flow.count

class PlaybackViewModel (private val localTrackViewModel: LocalTrackViewModel): ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val _currentTrack = MutableStateFlow<LocalTrack?>(null)
    val currentTrack: StateFlow<LocalTrack?> = _currentTrack.asStateFlow()


    private val _trackList = MutableStateFlow<List<LocalTrack>>(emptyList())
    val trackList: StateFlow<List<LocalTrack>> = _trackList.asStateFlow()
    // Получаем список треков из LocalTrackViewModel

    init {
        // Загружаем список треков при инициализации
        viewModelScope.launch {
            localTrackViewModel.tracks.collect { tracks ->
                Log.d("PlaybackViewModel", "Список треков загружен: ${tracks.size} треков")
                _trackList.value = tracks
            }
        }


    }

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Paused)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _trackDuration = MutableStateFlow(0L)
    val trackDuration: StateFlow<Long> = _trackDuration


    fun setTrack(track: LocalTrack) {
        Log.d("PlaybackViewModel", "Установлен трек: ${track.title}, путь: ${track.filePath}")
        _currentTrack.value = track
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.filePath) // Убедитесь, что filePath корректен
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _trackDuration.value = it.duration.toLong()
            Log.d("PlaybackViewModel", "Трек готов к воспроизведению: ${track.title}, длительность: ${it.duration} мс")
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
        val currentTrack = _currentTrack.value
        val trackList = _trackList.value

        if (currentTrack != null && trackList.isNotEmpty()) {
            val currentIndex = trackList.indexOf(currentTrack)
            val nextIndex = if (currentIndex < trackList.size - 1) currentIndex + 1 else 0

            val nextTrack = trackList[nextIndex]
            Log.d("PlaybackViewModel", "Переключение на следующий трек: ${nextTrack.title}")
            setTrack(nextTrack)
            play() // Автоматически воспроизводим следующий трек
        }
        else {
            Log.d("PlaybackViewModel", "Невозможно переключить трек: список треков пуст или текущий трек не найден")
        }
    }

    fun previousTrack() {
        val currentTrack = _currentTrack.value
        val trackList = _trackList.value

        if (currentTrack != null && trackList.isNotEmpty()) {
            val currentIndex = trackList.indexOf(currentTrack)
            val previousIndex = if (currentIndex > 0) currentIndex - 1 else trackList.size - 1

            val previousTrack = trackList[previousIndex]
            setTrack(previousTrack)
            play() // Автоматически воспроизводим предыдущий трек
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
}

