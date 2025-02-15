package com.example.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicapp.data.local.LocalTRepository
import com.example.musicapp.data.local.LocalTrack
import com.example.musicapp.ui.screens.ApiTracksScreen
import com.example.musicapp.ui.screens.LocalTracksScreen
import com.example.musicapp.ui.screens.PlaybackScreen
import com.example.musicapp.ui.screens.local.LocalTrackViewModel

@Composable
fun NavGraph(
    navHostController: NavHostController
){
    val context = LocalContext.current
    val repository = LocalTRepository(context)
    val viewModel = LocalTrackViewModel(repository)

    NavHost(navController = navHostController, startDestination = "local_tracks"){
        composable("local_tracks"){
            LocalTracksScreen(viewModel = viewModel, onTrackClick = { track ->
                navHostController.navigate("playback/${track.id}")
            })
        }
        composable("api_tracks"){
            ApiTracksScreen(
                onTrackClick = { track ->
                    navController.navigate("playback/${track.id}")
                }
            )
        }
        composable( route = "playback/{$trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId")
            PlaybackScreen(trackId = trackId)
        }
    }
}