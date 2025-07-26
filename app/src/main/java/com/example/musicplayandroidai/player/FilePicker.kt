package com.example.musicplayandroidai.player

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class FilePicker(
    private val launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>
) {
    fun pickAudio() {
        // Launch the system file picker requesting audio files
        launcher.launch(arrayOf("audio/*"))
    }
}

@Composable
fun rememberFilePicker(onFilePicked: (Uri?) -> Unit): FilePicker {
    // Create a launcher that survives recomposition and is registered at the correct lifecycle state
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = onFilePicked
    )
    // Provide a stable FilePicker instance
    return remember { FilePicker(launcher) }
}
