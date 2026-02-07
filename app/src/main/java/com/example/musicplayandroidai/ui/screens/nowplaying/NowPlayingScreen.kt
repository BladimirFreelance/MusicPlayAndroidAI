package com.example.musicplayandroidai.ui.screens.nowplaying

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.screens.library.Track
import com.example.musicplayandroidai.ui.theme.GlassBlack
import com.example.musicplayandroidai.ui.theme.GlassWhite
import com.example.musicplayandroidai.ui.theme.GlowDark
import com.example.musicplayandroidai.ui.theme.GlowLight
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme
import java.util.Locale

@Composable
fun NowPlayingScreen(playerManager: PlayerManager) {
    val currentTrack by playerManager.currentTrack
    val isPlaying by playerManager.isPlaying
    val position by playerManager.currentPosition
    val duration by playerManager.duration
    val shuffleEnabled by playerManager.shuffleModeEnabled
    val repeatMode by playerManager.repeatMode
    
    NowPlayingContent(
        currentTrack = currentTrack,
        isPlaying = isPlaying,
        position = position,
        duration = duration,
        shuffleEnabled = shuffleEnabled,
        repeatMode = repeatMode,
        onSeek = { playerManager.seekTo(it) },
        onPrevious = { playerManager.playPrevious() },
        onTogglePlayPause = { playerManager.togglePlayPause() },
        onNext = { playerManager.playNext() },
        onToggleShuffle = { playerManager.toggleShuffle() },
        onToggleRepeat = { playerManager.toggleRepeatMode() }
    )
}

@Composable
fun NowPlayingContent(
    currentTrack: Track?,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    onSeek: (Long) -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val glowColor = if (isDark) GlowDark else GlowLight
    val glassBg = if (isDark) GlassBlack.copy(alpha = 0.25f) else GlassWhite.copy(alpha = 0.4f)
    val glassBorder = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя панель (меню)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Три точки
//            IconButton(onClick = { /* TODO: Menu */ }) {
//                Icon(
//                    imageVector = Icons.Default.MoreHoriz,
//                    contentDescription = "Menu",
//                    tint = Color.White.copy(alpha = 0.7f)
//                )
//            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // 1. Обложка
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(40.dp))
                .background(glassBg)
        ) {
            if (currentTrack?.albumArtUri != null) {
                AsyncImage(
                    model = currentTrack.albumArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.2f))

        // 2. Инфо о треке
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentTrack?.title ?: "No Track Playing",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentTrack?.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.weight(0.3f))

        // 3. Стеклянная панель управления
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(glassBg)
                .border(1.dp, glassBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                // Прогресс
                Column {
                    Slider(
                        value = position.toFloat(),
                        onValueChange = { onSeek(it.toLong()) },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(position), color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text(formatTime(duration), color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Кнопки плеера
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    // Play/Pause в круге с легким свечением
                    Surface(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Default.SkipNext, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Нижняя панель (Shuffle и Repeat)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(glassBg)
                .border(1.dp, glassBorder, RoundedCornerShape(32.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка перемешать (Shuffle)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onToggleShuffle() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Shuffle, null,
                            tint = if (shuffleEnabled) glowColor else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Shuffle",
                            color = if (shuffleEnabled) glowColor else Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Разделитель
                Box(modifier = Modifier.width(1.dp).fillMaxHeight(0.4f).background(glassBorder))

                // Кнопка повтора (Repeat)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onToggleRepeat() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (repeatMode == Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            null,
                            tint = if (repeatMode != Player.REPEAT_MODE_OFF) glowColor else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Repeat",
                            color = if (repeatMode != Player.REPEAT_MODE_OFF) glowColor else Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.15f))
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
}

@Preview(showBackground = true)
@Composable
fun NowPlayingScreenPreview() {
    MusicPlayAndroidAITheme {
        Box(modifier = Modifier.background(Color.Black)) {
            NowPlayingContent(
                currentTrack = null,
                isPlaying = true,
                position = 30000L,
                duration = 180000L,
                shuffleEnabled = true,
                repeatMode = Player.REPEAT_MODE_OFF,
                onSeek = {},
                onPrevious = {},
                onTogglePlayPause = {},
                onNext = {},
                onToggleShuffle = {},
                onToggleRepeat = {}
            )
        }
    }
}
