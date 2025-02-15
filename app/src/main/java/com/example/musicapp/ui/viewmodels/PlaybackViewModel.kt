package com.example.musicapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaybackViewModel : ViewModel() {

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Paused)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _trackDuration = MutableStateFlow(0L)
    val trackDuration: StateFlow<Long> = _trackDuration

    fun play() {
        _playbackState.value = PlaybackState.Playing
    }

    fun pause() {
        _playbackState.value = PlaybackState.Paused
    }

    fun seekTo(position: Long) {
        _currentPosition.value = position
    }

    fun setTrackDuration(duration: Long) {
        _trackDuration.value = duration
    }

    fun nextTrack() {
        // Logic to switch to the next track
    }

    fun previousTrack() {
        // Logic to switch to the previous track
    }
}

sealed class PlaybackState {
    object Playing : PlaybackState()
    object Paused : PlaybackState()
}