package com.example.musicapp.ui.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.MainActivity.Companion.REQUEST_CODE
import com.example.musicapp.data.repository.LocalTRepository
import com.example.musicapp.data.LocalTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocalTrackViewModel(private val repository: LocalTRepository) : ViewModel() {


    private val _tracks = MutableStateFlow<List<LocalTrack>>(emptyList())
    val tracks: StateFlow<List<LocalTrack>> = _tracks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredTracks = MutableStateFlow<List<LocalTrack>>(emptyList())
    val filteredTracks: StateFlow<List<LocalTrack>> = _filteredTracks.asStateFlow()

    /*init {
        loadTracks()
    }*/

    fun loadTracks() {
        viewModelScope.launch {
            _tracks.value = repository.getAllTracks()
            _filteredTracks.value = _tracks.value
        }
    }




     fun searchTracks(query: String) {

         viewModelScope.launch {
             _searchQuery.value = query
             _filteredTracks.value = repository.searchTracks(query)
         }
    }

    private val _currentTrack = MutableStateFlow<LocalTrack?>(null)
    val currentTrack: StateFlow<LocalTrack?> = _currentTrack.asStateFlow()

    fun getTrackById(trackId: Long) {
        viewModelScope.launch {
            _currentTrack.value = repository.getLocalTrackById(trackId)
        }
    }
}