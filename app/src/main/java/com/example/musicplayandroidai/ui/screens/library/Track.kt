package com.example.musicplayandroidai.ui.screens.library

import android.net.Uri

/**
 * Модель данных для музыкального трека.
 * @param dateAdded Дата добавления трека в систему (в секундах), используется для сортировки.
 */
data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null,
    val dateAdded: Long = 0L // Новое поле для сортировки по дате
)
