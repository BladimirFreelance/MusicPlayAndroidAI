package com.example.musicplayandroidai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.theme.GlassBlack
import com.example.musicplayandroidai.ui.theme.GlassWhite
import java.util.Locale

/**
 * Компактный плеер (MiniPlayer), отображаемый над нижней навигацией.
 */
@Composable
fun MiniPlayer(
    playerManager: PlayerManager,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Подписываемся на состояния плеера
    val currentTrack by playerManager.currentTrack
    val isPlaying by playerManager.isPlaying
    val position by playerManager.currentPosition
    val duration by playerManager.duration

    // Настройки стилей "стекла"
    val glassColor = if (isSystemInDarkTheme()) GlassBlack else GlassWhite
    val borderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f)

    if (currentTrack == null) return

    // Расчет прогресса для полоски (от 0.0 до 1.0)
    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(110.dp) // Немного увеличим высоту для текстов времени
            .clip(RoundedCornerShape(24.dp))
            .background(glassColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- ОБЛОЖКА ТРЕКА ---
                AsyncImage(
                    model = currentTrack?.albumArtUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // --- ИНФОРМАЦИЯ (Название и автор) ---
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack?.title ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack?.artist ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // --- КНОПКИ УПРАВЛЕНИЯ ---
                IconButton(onClick = { playerManager.togglePlayPause() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { playerManager.playNext() }) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- ПРОГРЕСС-БАР ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f)) // Реальный прогресс
                        .fillMaxHeight()
                        .background(Color(0xFF00E5FF)) // Бирюзовый цвет индикатора
                )
            }
            
            // --- ВРЕМЯ (Текущее / Всего) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(position),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Функция для форматирования времени в mm:ss
 */
private fun formatTime(ms: Long): String {
    if (ms < 0) return "0:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
