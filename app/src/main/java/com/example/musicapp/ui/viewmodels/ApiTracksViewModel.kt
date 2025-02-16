package com.example.musicapp.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.Track
import com.example.musicapp.data.api.DeezerApi
import kotlinx.coroutines.launch

class ApiTracksViewModel : ViewModel() {

    private val _tracks = mutableStateListOf<Track>()
    val tracks: List<Track> get() = _tracks

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = DeezerApi.retrofitService.searchTracks(query)
                _tracks.clear()
                _tracks.addAll(response.data.map { track ->
                    Track(
                        id = track.id,
                        title = track.title,
                        artist = track.artist.name,
                        album = track.album.cover_medium,
                        preview = track.preview
                    )
                })
            } catch (e: Exception) {
                _error.value = e.message
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
                // Вызов метода getTopTracks() через DeezerApi
                val response = DeezerApi.retrofitService.getTopTracks()
                if (response.isSuccessful) {
                    _tracks.clear()
                    response.body()?.tracks?.data?.forEach { track ->
                        _tracks.add(
                            Track(
                                id = track.id,
                                title = track.title,
                                artist = track.artist.name,
                                album = track.album.cover_medium,
                                preview = track.preview
                            )
                        )
                    }
                } else {
                    _error.value = "Failed to load tracks: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}