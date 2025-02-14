package com.example.musicapp.navigation


import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItemDefaults.colors
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigation(
    navController: NavController
) {
    val listItems = listOf(BottomItem.LocalTracks,BottomItem.LocalTracks)
    NavigationBar(
        containerColor = Color.White
    )
    {
        val backStackEntry by navController.currentBackStackEntryAsState()
        var currentRoute = backStackEntry?.destination?.route
        listItems.forEach{ item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = "Icon")
                },
                label = {
                    Text(text = item.title, fontSize = 9.sp)
                },
                colors = colors(
                selectedIconColor = Color.Blue, // Цвет выбранной иконки
                unselectedIconColor = Color.Gray, // Цвет невыбранной иконки
                selectedTextColor = Color.Blue, // Цвет выбранного текста
                unselectedTextColor = Color.Gray // Цвет невыбранного текста
                )
            )
        }
    }
}


