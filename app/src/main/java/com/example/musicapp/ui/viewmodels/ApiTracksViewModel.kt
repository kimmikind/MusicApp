package com.example.musicapp.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.ApiTrack
import com.example.musicapp.data.LocalTrack
import com.example.musicapp.data.Track
import com.example.musicapp.data.api.DeezerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApiTracksViewModel : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    fun loadTrackById(trackId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = DeezerApi.retrofitService.getTrackById(trackId)
                if (response.isSuccessful) {
                    val apiTrack = response.body()
                    apiTrack?.let {
                        _currentTrack.value = Track(
                            id = it.id,
                            title = it.title,
                            artist = it.artist.name,
                            coverUrl = it.album.cover_medium,
                            previewUrl = it.preview
                        )
                    }
                } else {
                    _error.value = "Failed to load track: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = DeezerApi.retrofitService.searchTracks(query)
                _tracks.value = response.body()?.data?.map { apiTrack :ApiTrack ->
                    Track(
                        id = apiTrack.id,
                        title = apiTrack.title,
                        artist = apiTrack.artist.name,
                        coverUrl = apiTrack.album.cover_medium,
                        previewUrl = apiTrack.preview
                    )
                } ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTopTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = DeezerApi.retrofitService.getTopTracks()
                if (response.isSuccessful) {
                    _tracks.value = response.body()?.tracks?.data?.map { apiTrack :ApiTrack  ->
                        Track(
                            id = apiTrack.id,
                            title = apiTrack.title,
                            artist = apiTrack.artist.name,
                            coverUrl = apiTrack.album.cover_medium,
                            previewUrl = apiTrack.preview
                        )

                    } ?: emptyList()

                } else {
                    _error.value = "Failed to load tracks: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}