package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LocalTracksScreen() {
    Text(
        modifier = Modifier.fillMaxSize().wrapContentSize(),
        text = "Local",
        textAlign = TextAlign.Center
    )
}