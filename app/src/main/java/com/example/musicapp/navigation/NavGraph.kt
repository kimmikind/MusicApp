package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.ui.screens.ApiTracksScreen
import com.example.musicapp.ui.screens.LocalTracksScreen
import com.example.musicapp.ui.screens.PlaybackScreen

@Composable
fun NavGraph(
    navHostController: NavHostController
){
    NavHost(navController = navHostController, startDestination = "local_tracks"){
        composable("local_tracks"){
            LocalTracksScreen()
        }
        composable("api_tracks"){
            ApiTracksScreen()
        }
        composable("playback_screen"){
            PlaybackScreen()
        }
    }
}