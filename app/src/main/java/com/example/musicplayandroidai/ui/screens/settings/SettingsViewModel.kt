package com.example.musicplayandroidai.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayandroidai.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager.getInstance(application)

    val isAudioFocusEnabled: StateFlow<Boolean> = settingsManager.isAudioFocusEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAudioFocusEnabled(enabled: Boolean) {
        settingsManager.setAudioFocusEnabled(enabled)
    }
}
