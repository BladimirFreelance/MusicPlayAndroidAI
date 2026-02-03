package com.example.musicplayandroidai.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.musicplayandroidai.ui.navigation.AppNavHost
import com.example.musicplayandroidai.ui.navigation.GlassDock

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val isDockExpanded = rememberSaveable { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavHost(navController = navController)
        GlassDock(
            navController = navController,
            isExpanded = isDockExpanded.value,
            onToggleExpanded = { isDockExpanded.value = !isDockExpanded.value }
        )
    }
}
