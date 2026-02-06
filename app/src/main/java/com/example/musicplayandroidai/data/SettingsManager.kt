package com.example.musicplayandroidai.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isAudioFocusEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUDIO_FOCUS, true))
    val isAudioFocusEnabled: StateFlow<Boolean> = _isAudioFocusEnabled

    fun setAudioFocusEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUDIO_FOCUS, enabled).apply()
        _isAudioFocusEnabled.value = enabled
    }

    companion object {
        private const val KEY_AUDIO_FOCUS = "audio_focus_enabled"
        
        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context).also { INSTANCE = it }
            }
        }
    }
}
