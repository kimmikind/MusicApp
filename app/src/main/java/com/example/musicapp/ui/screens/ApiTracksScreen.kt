package com.example.musicapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.musicapp.data.LocalTrack

@Composable
fun ApiTracksScreen(
) {
    Text(
        modifier = Modifier.fillMaxSize().wrapContentSize(),
        text = "Api",
        textAlign = TextAlign.Center
    )
}