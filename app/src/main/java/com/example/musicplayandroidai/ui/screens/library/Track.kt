package com.example.musicplayandroidai.ui.screens.library

import android.net.Uri

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null
)
