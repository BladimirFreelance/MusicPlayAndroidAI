package com.example.musicplayandroidai.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.musicplayandroidai.ui.screens.library.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Репозиторий для получения аудиофайлов из хранилища устройства.
 */
class TracksRepository(private val context: Context) {

    /**
     * Получает список всех музыкальных треков.
     */
    suspend fun getAllTracks(): List<Track> = withContext(Dispatchers.IO) {
        val trackList = mutableListOf<Track>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED // Добавляем дату для сортировки
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        // Получаем данные через ContentResolver
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null // Сортировку будем делать в ViewModel
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Unknown"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val duration = cursor.getLong(durationColumn)
                val dateAdded = cursor.getLong(dateColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                
                trackList.add(Track(id, title, artist, duration, uri, null, dateAdded))
            }
        }
        trackList
    }

    suspend fun getTrackById(trackId: Long): Track? {
        return getAllTracks().find { it.id == trackId }
    }
}
