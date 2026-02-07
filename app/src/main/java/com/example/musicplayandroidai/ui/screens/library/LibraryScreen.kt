package com.example.musicplayandroidai.ui.screens.library

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicplayandroidai.R
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme
import java.util.Locale

/**
 * Основной экран библиотеки музыки.
 */
@Composable
fun LibraryScreen(
    playerManager: PlayerManager,
    onNavigateToNowPlaying: () -> Unit
) {
    val viewModel: LibraryViewModel = viewModel()
    val tracks by viewModel.tracks.collectAsState()
    val currentTrack by playerManager.currentTrack
    val currentSortType by viewModel.sortType.collectAsState()
    val context = LocalContext.current

    // Определение нужного разрешения в зависимости от версии Android
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(context.checkSelfPermission(permissionToRequest) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    // Лаунчер для запроса разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                hasPermission = true
                viewModel.loadTracks()
            }
        }
    )

    // Загрузка данных при старте или получении разрешения
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.loadTracks()
        } else {
            permissionLauncher.launch(permissionToRequest)
        }
    }

    LibraryContent(
        tracks = tracks,
        selectedTrackId = currentTrack?.id,
        hasPermission = hasPermission,
        currentSortType = currentSortType,
        onSortClick = { viewModel.setSortType(it) },
        onTrackClick = { track ->
            playerManager.playTrack(track, tracks)
        }
    )
}

/**
 * Контент экрана библиотеки: список, табы и сортировка.
 */
@Composable
fun LibraryContent(
    tracks: List<Track>,
    selectedTrackId: Long?,
    hasPermission: Boolean,
    currentSortType: SortType,
    onSortClick: (SortType) -> Unit,
    onTrackClick: (Track) -> Unit
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    
    // Переменная для предотвращения "прыжков" списка при ручном клике
    var lastClickedTrackId by remember { mutableStateOf<Long?>(null) }

    // Эффект автоматического центрирования играющего трека
    LaunchedEffect(selectedTrackId, tracks) {
        if (selectedTrackId != null && tracks.isNotEmpty()) {
            if (selectedTrackId == lastClickedTrackId) {
                lastClickedTrackId = null 
                return@LaunchedEffect
            }

            val index = tracks.indexOfFirst { it.id == selectedTrackId }
            if (index != -1) {
                val viewportHeight = listState.layoutInfo.viewportSize.height
                if (viewportHeight > 0) {
                    val itemHeight = listState.layoutInfo.visibleItemsInfo
                        .find { it.index == index }?.size ?: with(density) { 76.dp.roundToPx() }
                    
                    val offset = (viewportHeight / 2) - (itemHeight / 2)
                    listState.animateScrollToItem(index, -offset)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- ТАБЫ (Вкладки) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(44.dp)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf("All", "Playlists", "", "")
            var selectedTab by remember { mutableStateOf("All") }

            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { selectedTab = tab },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- БЛОК СОРТИРОВКИ ---
        var showSortMenu by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    modifier = Modifier.clickable { showSortMenu = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort: ${currentSortType.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        painter = painterResource(id = android.R.drawable.arrow_down_float),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp).padding(start = 4.dp)
                    )
                }
                
                // Выпадающее меню выбора сортировки
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(Color(0xFF2B2B2B))
                ) {
                    SortType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName, color = Color.White) },
                            onClick = {
                                onSortClick(type)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // --- СПИСОК ТРЕКОВ ---
        if (hasPermission) {
            if (tracks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No music found", color = Color.White.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 180.dp)
                ) {
                    itemsIndexed(tracks, key = { _, track -> track.id }) { _, track ->
                        TrackItem(
                            track = track,
                            isSelected = track.id == selectedTrackId,
                            onClick = {
                                lastClickedTrackId = track.id
                                onTrackClick(track)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Элемент списка (один трек).
 */
@Composable
private fun TrackItem(track: Track, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.albumArtUri,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = formatDuration(track.duration),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Форматирование длительности в mm:ss.
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    MusicPlayAndroidAITheme {
        Box(modifier = Modifier.background(Color.Black)) {
            LibraryContent(
                tracks = listOf(
                    Track(1, "Dream Escape", "Lumina", 204000, Uri.EMPTY),
                    Track(2, "Starlight Avenue", "Synthwave Dreams", 241000, Uri.EMPTY),
                    Track(3, "Chill Vibes", "Serenity", 224000, Uri.EMPTY)
                ),
                selectedTrackId = 1L,
                hasPermission = true,
                currentSortType = SortType.NAME,
                onSortClick = {},
                onTrackClick = {}
            )
        }
    }
}
