package com.example.musicplayandroidai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.components.MiniPlayer
import com.example.musicplayandroidai.ui.navigation.AppDestination
import com.example.musicplayandroidai.ui.navigation.AppNavHost
import com.example.musicplayandroidai.ui.navigation.GlassDock
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme

/**
 * Основная точка входа в UI приложения.
 */
@Composable
fun AppRoot(playerManager: PlayerManager) {
    val navController = rememberNavController()
    AppRootContent(navController = navController, playerManager = playerManager)
}

/**
 * Главный контейнер приложения.
 */
@Composable
fun AppRootContent(
    navController: NavHostController,
    playerManager: PlayerManager,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Список главных экранов (где есть нижняя панель)
    val mainRoutes = listOf(
        AppDestination.Library.route,
        AppDestination.Playlists.route,
        AppDestination.Settings.route
    )
    
    // Проверка, на основном ли мы экране
    val isMainScreen = currentRoute == null || currentRoute in mainRoutes
    val isNowPlaying = currentRoute == AppDestination.NowPlaying.route

    val backgroundResId = if (isDarkTheme) R.drawable.dark_tm else R.drawable.light_tm

    Box(modifier = modifier.fillMaxSize()) {
        // 1. ФОН
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. КОНТЕНТ (Экраны навигации)
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(navController = navController, playerManager = playerManager)
            }
            
            // 3. НИЖНЯЯ ПАНЕЛЬ УПРАВЛЕНИЯ
            if (!isLandscape && !isNowPlaying) {
                val currentTrack by playerManager.currentTrack
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Мини-плеер показываем везде, кроме экрана плеера NowPlaying
                    if (currentTrack != null) {
                        MiniPlayer(
                            playerManager = playerManager,
                            onClick = { navController.navigate(AppDestination.NowPlaying.route) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Док-панель (кнопки Library/Playlists) показываем только на главных экранах
                    if (isMainScreen) {
                        GlassDock(
                            navController = navController,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        // 4. КНОПКА "НАЗАД"
        // Она должна быть видна на ВСЕХ экранах, кроме главных (Library, Playlists, Settings)
        // Включая экран NowPlayingScreen
        if (!isMainScreen && currentRoute != null) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
