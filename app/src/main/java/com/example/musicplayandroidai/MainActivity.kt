package com.example.musicplayandroidai

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.player.rememberFilePicker
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme
import com.example.musicplayandroidai.AppLogger

class MainActivity : ComponentActivity() {
    private lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.d("App started")
        enableEdgeToEdge()
        playerManager = PlayerManager(applicationContext)
        setContent {
            MusicPlayAndroidAITheme {
                MusicPlayerScreen(playerManager)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        playerManager.release()
    }
}

@Composable
fun MusicPlayerScreen(playerManager: PlayerManager) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val filePicker = rememberFilePicker { uri ->
        selectedUri = uri
        fileName = uri?.lastPathSegment
        uri?.let { playerManager.prepare(it) }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            errorMessage = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Musicplay")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                AppLogger.d("Select file button clicked")
                filePicker.pickAudio()
            }) {
                Text("Выбрать аудиофайл")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    AppLogger.d("Play clicked")
                    if (selectedUri != null) {
                        playerManager.play()
                    } else {
                        errorMessage = "Файл не выбран"
                        AppLogger.e("Playback error: file not selected")
                    }
                }) { Text("Play") }
                Button(onClick = {
                    AppLogger.d("Pause clicked")
                    playerManager.pause()
                }) { Text("Pause") }
                Button(onClick = {
                    AppLogger.d("Stop clicked")
                    playerManager.stop()
                }) { Text("Stop") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = fileName ?: "Файл не выбран")
        }
    }
}

