package com.example.musicplayandroidai.player

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class FilePicker(private val launcher: ActivityResultLauncher<Array<String>>) {
    fun pickAudio() {
        launcher.launch(arrayOf("audio/*"))
    }
}

@Composable
fun rememberFilePicker(onFilePicked: (Uri?) -> Unit): FilePicker {
    val activity = LocalContext.current as ComponentActivity
    val launcher = remember(activity) {
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument(), onFilePicked)
    }
    DisposableEffect(Unit) {
        onDispose { launcher.unregister() }
    }
    return remember { FilePicker(launcher) }
}
