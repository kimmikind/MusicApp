package com.example.musicapp.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicapp.data.repository.LocalTRepository
import com.example.musicapp.ui.screens.ApiTracksScreen
import com.example.musicapp.ui.screens.LocalTracksScreen
import com.example.musicapp.ui.screens.PlaybackScreen
import com.example.musicapp.ui.viewmodels.ApiTracksViewModel
import com.example.musicapp.ui.viewmodels.LocalTrackViewModel
import com.example.musicapp.ui.viewmodels.PlaybackViewModel

@Composable
fun NavGraph(
    navHostController: NavHostController
){
    val context = LocalContext.current
    val localRepository = LocalTRepository(context)
    val localViewModel = LocalTrackViewModel(localRepository)
    val apiViewModel = ApiTracksViewModel()
    val playbackViewModel = PlaybackViewModel(localViewModel, apiViewModel,context)

    NavHost(navController = navHostController, startDestination = "local_tracks"){
        composable("local_tracks"){
            LocalTracksScreen(viewModel = localViewModel, onTrackClick = { track ->
                navHostController.navigate("playback/${track.id}/local")
            })
        }
        composable("api_tracks"){
            ApiTracksScreen( viewModel = apiViewModel,
                onTrackClick = {  track ->
                    navHostController.navigate("playback/${track.id}/api")
                }
            )
        }
        composable(
            route = "playback/{trackId}/{source}",
            arguments = listOf(
                navArgument("trackId") { type = NavType.LongType },
                navArgument("source") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId")
            val source = backStackEntry.arguments?.getString("source")
            PlaybackScreen(trackId = trackId, source = source, localTrackViewModel = localViewModel,
                playbackViewModel = playbackViewModel, apiTrackViewModel = apiViewModel)
        }
    }
}