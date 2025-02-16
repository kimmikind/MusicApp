package com.example.musicapp.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.navigation.BottomNavigation
import com.example.musicapp.navigation.NavGraph
import com.example.musicapp.service.MusicService

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    // Запуск сервиса при инициализации
    LaunchedEffect(Unit) {
        val intent = Intent(context, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) }
    ){ innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navHostController = navController)
        }
    }

}