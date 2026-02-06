package com.example.musicplayandroidai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.navigation.AppDestination
import com.example.musicplayandroidai.ui.navigation.AppNavHost
import com.example.musicplayandroidai.ui.navigation.GlassDock

@Composable
fun AppRoot(playerManager: PlayerManager) {
    val navController = rememberNavController()
    AppRootContent(
        navController = navController,
        playerManager = playerManager
    )
}

@Composable
fun AppRootContent(
    navController: NavHostController,
    playerManager: PlayerManager? = null,
    modifier: Modifier = Modifier
) {
    // Устанавливаем начальное состояние развернутым (true)
    // Благодаря rememberSaveable, оно будет сбрасываться в true ПРИ КАЖДОМ ХОЛОДНОМ ЗАПУСКЕ приложения.
    // При повороте экрана состояние будет сохраняться.
    val isDockExpanded = rememberSaveable { mutableStateOf(true) }
    
    val isDarkTheme = isSystemInDarkTheme()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBackButton = currentRoute != AppDestination.Library.route &&
                         currentRoute != AppDestination.Playlists.route &&
                         currentRoute != AppDestination.Settings.route &&
                         currentRoute != null

    val backgroundResId = if (isDarkTheme) {
        R.drawable.dark_tm
    } else {
        R.drawable.light_tm
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (playerManager != null) {
            AppNavHost(navController = navController, playerManager = playerManager)
        }

        if (showBackButton) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        if (isDockExpanded.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isDockExpanded.value = false
                    }
            )
        }

        GlassDock(
            navController = navController,
            isExpanded = isDockExpanded.value,
            onToggleExpanded = { isDockExpanded.value = !isDockExpanded.value },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
