package com.example.musicplayandroidai.ui.navigation

sealed class AppDestination(
    val route: String,
    val label: String
) {
    data object Library : AppDestination("library", "Library")
    data object Playlists : AppDestination("playlists", "Playlists")
    data object Settings : AppDestination("settings", "Settings")
    data object NowPlaying : AppDestination("nowPlaying", "Now Playing")
}

val bottomBarDestinations = listOf(
    AppDestination.Library,
    AppDestination.Playlists,
    AppDestination.Settings
)
