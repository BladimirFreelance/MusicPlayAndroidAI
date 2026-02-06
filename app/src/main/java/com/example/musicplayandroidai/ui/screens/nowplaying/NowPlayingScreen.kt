package com.example.musicplayandroidai.ui.screens.nowplaying

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.theme.GlassBlack
import com.example.musicplayandroidai.ui.theme.GlassWhite

@Composable
fun NowPlayingScreen(playerManager: PlayerManager) {
    val currentTrack = playerManager.currentTrack.value
    val isPlaying = playerManager.isPlaying.value
    val glassColor = if (isSystemInDarkTheme()) GlassBlack else GlassWhite

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Карточка обложки (заглушка)
        Surface(
            modifier = Modifier
                .size(300.dp)
                .aspectRatio(1f),
            shape = RoundedCornerShape(24.dp),
            color = glassColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "No Art",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Информация о треке
        Text(
            text = currentTrack?.title ?: "No track playing",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = currentTrack?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки управления (заглушка)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(onClick = { playerManager.togglePlayPause() }, modifier = Modifier.size(64.dp)) {
                // В реальном проекте здесь были бы иконки Play/Pause
                Text(
                    text = if (isPlaying) "PAUSE" else "PLAY",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
