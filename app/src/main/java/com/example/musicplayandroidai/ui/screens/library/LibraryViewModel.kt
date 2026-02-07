package com.example.musicplayandroidai.ui.screens.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayandroidai.data.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Типы сортировки треков.
 */
enum class SortType(val displayName: String) {
    NAME("Name"),
    DATE("Date Added"),
    DURATION("Duration")
}

/**
 * ViewModel для управления данными экрана библиотеки.
 */
class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TracksRepository(application)

    // Исходный список всех треков
    private var allTracks = emptyList<Track>()

    // Отфильтрованный и отсортированный список для UI
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    // Текущий тип сортировки
    private val _sortType = MutableStateFlow(SortType.NAME)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    /**
     * Загрузка треков из репозитория.
     */
    fun loadTracks() {
        viewModelScope.launch {
            allTracks = repository.getAllTracks()
            applySort() // Применяем сортировку после загрузки
        }
    }

    /**
     * Изменение типа сортировки.
     */
    fun setSortType(type: SortType) {
        _sortType.value = type
        applySort()
    }

    /**
     * Логика сортировки списка.
     */
    private fun applySort() {
        _tracks.value = when (_sortType.value) {
            SortType.NAME -> allTracks.sortedBy { it.title.lowercase() }
            SortType.DATE -> allTracks.sortedByDescending { it.dateAdded } // Новые сверху
            SortType.DURATION -> allTracks.sortedByDescending { it.duration } // Длинные сверху
        }
    }
}
