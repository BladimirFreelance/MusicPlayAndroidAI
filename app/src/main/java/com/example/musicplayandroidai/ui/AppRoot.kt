package com.example.musicplayandroidai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.ui.navigation.AppNavHost
import com.example.musicplayandroidai.ui.navigation.GlassDock
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val isDockExpanded = rememberSaveable { mutableStateOf(true) }
    val isDarkTheme = isSystemInDarkTheme()

    // Выбираем фоновое изображение в зависимости от темы
    val backgroundResId = if (isDarkTheme) {
        R.drawable.dark_tm
    } else {
        R.drawable.light_tm
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение на весь экран
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null, // Декоративный элемент
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Заполняем весь экран, обрезая лишнее
        )

        // Контент приложения
        AppNavHost(navController = navController)

        // Стеклянная навигационная панель
        GlassDock(
            navController = navController,
            isExpanded = isDockExpanded.value,
            onToggleExpanded = { isDockExpanded.value = !isDockExpanded.value },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppRootPreview() {
    MusicPlayAndroidAITheme {
        AppRoot()
    }
}
