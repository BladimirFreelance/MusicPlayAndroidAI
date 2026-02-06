package com.example.musicplayandroidai.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playback_persistence")

class PlaybackPersistenceManager(private val context: Context) {

    companion object {
        private val LAST_TRACK_ID = longPreferencesKey("last_track_id")
        private val LAST_POSITION = longPreferencesKey("last_position")
        private val LAST_QUEUE = stringPreferencesKey("last_queue")
    }

    val lastTrackId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[LAST_TRACK_ID]
    }

    val lastPosition: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LAST_POSITION] ?: 0L
    }

    val lastQueue: Flow<List<Long>> = context.dataStore.data.map { preferences ->
        val queueString = preferences[LAST_QUEUE] ?: ""
        if (queueString.isEmpty()) emptyList()
        else queueString.split(",").mapNotNull { it.toLongOrNull() }
    }

    suspend fun savePlaybackState(trackId: Long?, position: Long, queue: List<Long> = emptyList()) {
        context.dataStore.edit { preferences ->
            if (trackId != null) {
                preferences[LAST_TRACK_ID] = trackId
            }
            preferences[LAST_POSITION] = position
            if (queue.isNotEmpty()) {
                preferences[LAST_QUEUE] = queue.joinToString(",")
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
