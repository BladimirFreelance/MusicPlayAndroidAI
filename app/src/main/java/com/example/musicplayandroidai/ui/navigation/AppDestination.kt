package com.example.musicplayandroidai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector // Заглушка для иконки
) {
    data object Library : AppDestination("library", "Library", Icons.Default.Home)
    data object Playlists : AppDestination("playlists", "Playlists", Icons.AutoMirrored.Filled.List)
    data object Settings : AppDestination("settings", "Settings", Icons.Default.Settings)
    data object NowPlaying : AppDestination("nowPlaying", "Now Playing", Icons.Default.Home)
}

val bottomBarDestinations = listOf(
    AppDestination.Library,
    AppDestination.Playlists,
    AppDestination.Settings
)
