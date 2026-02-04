package com.example.musicplayandroidai.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.musicplayandroidai.ui.navigation.AppNavHost
import com.example.musicplayandroidai.ui.navigation.GlassDock
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val isDockExpanded = rememberSaveable { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavHost(navController = navController)
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            GlassDock(
                navController = navController,
                isExpanded = isDockExpanded.value,
                onToggleExpanded = { isDockExpanded.value = !isDockExpanded.value }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRootPreview() {
    MusicPlayAndroidAITheme {
        AppRoot()
    }
}
