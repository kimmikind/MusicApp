package com.example.musicapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import com.example.musicapp.R

sealed class BottomItem(val title: String, val iconId: Int, val route: String) {
    object LocalTracks : BottomItem("LocalTracks", R.drawable.local_img, "local_tracks")
    object ApiTracks : BottomItem("LocalTracks", R.drawable.api_img, "api_tracks")

}