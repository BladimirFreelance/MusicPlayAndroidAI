package com.example.musicplayandroidai.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playback_state")

class PlaybackPersistenceManager(private val context: Context) {

    private val LAST_TRACK_ID = longPreferencesKey("last_track_id")
    private val LAST_POSITION = longPreferencesKey("last_position")
    private val LAST_QUEUE = stringPreferencesKey("last_queue")
    private val SHUFFLE_MODE_ENABLED = booleanPreferencesKey("shuffle_mode_enabled")
    private val REPEAT_MODE = intPreferencesKey("repeat_mode")

    val lastTrackId: Flow<Long?> = context.dataStore.data.map { it[LAST_TRACK_ID] }
    val lastPosition: Flow<Long> = context.dataStore.data.map { it[LAST_POSITION] ?: 0L }
    val lastQueue: Flow<List<Long>> = context.dataStore.data.map {
        val stringQueue = it[LAST_QUEUE]
        if (stringQueue.isNullOrEmpty()) emptyList()
        else stringQueue.split(",").mapNotNull { idStr -> idStr.toLongOrNull() }
    }
    val shuffleModeEnabled: Flow<Boolean> = context.dataStore.data.map { it[SHUFFLE_MODE_ENABLED] ?: false }
    val repeatMode: Flow<Int> = context.dataStore.data.map { it[REPEAT_MODE] ?: 0 } // 0 = REPEAT_MODE_OFF

    suspend fun savePlaybackState(
        trackId: Long?,
        position: Long,
        queueIds: List<Long>,
        isShuffleEnabled: Boolean,
        repeatMode: Int
    ) {
        context.dataStore.edit {
            if (trackId != null) {
                it[LAST_TRACK_ID] = trackId
            } else {
                it.remove(LAST_TRACK_ID)
            }
            it[LAST_POSITION] = position
            it[LAST_QUEUE] = queueIds.joinToString(",")
            it[SHUFFLE_MODE_ENABLED] = isShuffleEnabled
            it[REPEAT_MODE] = repeatMode
        }
    }
}
