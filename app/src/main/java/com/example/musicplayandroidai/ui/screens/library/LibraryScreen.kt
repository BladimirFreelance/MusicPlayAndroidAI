package com.example.musicplayandroidai.ui.screens.library

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.theme.GlassBlack
import com.example.musicplayandroidai.ui.theme.GlassWhite
import com.example.musicplayandroidai.ui.theme.GlowDark
import com.example.musicplayandroidai.ui.theme.GlowLight

@Composable
fun LibraryScreen(
    playerManager: PlayerManager,
    onNavigateToNowPlaying: () -> Unit
) {
    val viewModel: LibraryViewModel = viewModel()
    val tracks by viewModel.tracks.collectAsState()
    val context = LocalContext.current
    val currentTrack by playerManager.currentTrack
    val listState = rememberLazyListState()

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(context.checkSelfPermission(permissionToRequest) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                hasPermission = true
                viewModel.loadTracks()
            }
        }
    )

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.loadTracks()
        } else {
            permissionLauncher.launch(permissionToRequest)
        }
    }

    if (hasPermission) {
        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No music found on device.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenHeight = maxHeight
                val itemHeight = 84.dp
                val centerOffset = with(LocalDensity.current) {
                    ((screenHeight - itemHeight) / 2).toPx().toInt()
                }

                LaunchedEffect(currentTrack) {
                    if (tracks.isNotEmpty() && currentTrack != null) {
                        val index = tracks.indexOfFirst { it.id == currentTrack?.id }
                        if (index >= 0) {
                            listState.animateScrollToItem(index, scrollOffset = -centerOffset)
                        }
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 150.dp)
                ) {
                    itemsIndexed(tracks, key = { _, track -> track.id }) { _, track ->
                        val isSelected = currentTrack?.id == track.id
                        TrackItem(
                            track = track,
                            isSelected = isSelected,
                            onClick = {
                                playerManager.playTrack(track, tracks)
                                onNavigateToNowPlaying()
                            }
                        )
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Permission needed to read music", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* LaunchedEffect handles it */ }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
private fun TrackItem(track: Track, isSelected: Boolean, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val glassColor = if (isDark) GlassBlack else GlassWhite
    val glowColor = if (isDark) GlowDark else GlowLight
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(glassColor, shape)
            .then(
                if (isSelected) Modifier.border(BorderStroke(2.dp, glowColor), shape)
                else Modifier
            )
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
