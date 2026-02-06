package com.example.musicplayandroidai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.screens.library.LibraryScreen
import com.example.musicplayandroidai.ui.screens.nowplaying.NowPlayingScreen
import com.example.musicplayandroidai.ui.screens.playlists.PlaylistsScreen
import com.example.musicplayandroidai.ui.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    playerManager: PlayerManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Library.route,
        modifier = modifier
    ) {
        composable(AppDestination.Library.route) { 
            LibraryScreen(
                playerManager = playerManager,
                onNavigateToNowPlaying = {
                    navController.navigate(AppDestination.NowPlaying.route)
                }
            ) 
        }
        composable(AppDestination.Playlists.route) { PlaylistsScreen() }
        composable(AppDestination.Settings.route) { SettingsScreen() }
        composable(AppDestination.NowPlaying.route) { 
            NowPlayingScreen(playerManager = playerManager) 
        }
    }
}
