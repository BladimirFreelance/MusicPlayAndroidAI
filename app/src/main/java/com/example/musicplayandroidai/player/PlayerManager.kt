package com.example.musicplayandroidai.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayandroidai.ui.screens.library.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*

/**
 * Менеджер управления плеером.
 * Связывает UI с Media3 Exoplayer через MediaController.
 */
class PlayerManager(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    // Храним текущий список воспроизведения для надежности получения данных о треке
    private var currentPlaylist: List<Track> = emptyList()
    
    // --- СОСТОЯНИЯ ДЛЯ UI ---
    private val _currentTrack = mutableStateOf<Track?>(null)
    val currentTrack: State<Track?> = _currentTrack

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableLongStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableLongStateOf(0L)
    val duration: State<Long> = _duration

    private val _shuffleModeEnabled = mutableStateOf(false)
    val shuffleModeEnabled: State<Boolean> = _shuffleModeEnabled

    private val _repeatMode = mutableStateOf(Player.REPEAT_MODE_OFF)
    val repeatMode: State<Int> = _repeatMode

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    init {
        setupController()
    }

    /**
     * Инициализация MediaController для связи с PlaybackService
     */
    private fun setupController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.addListener(object : Player.Listener {
                // Вызывается при смене трека
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    // Пытаемся получить объект Track из тега или найти в текущем плейлисте по ID
                    val trackFromTag = mediaItem?.localConfiguration?.tag as? Track
                    if (trackFromTag != null) {
                        _currentTrack.value = trackFromTag
                    } else if (mediaItem != null) {
                        _currentTrack.value = currentPlaylist.find { it.id.toString() == mediaItem.mediaId }
                    } else {
                        // Если mediaItem совсем пустой, сбрасываем текущий трек
                        _currentTrack.value = null
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _shuffleModeEnabled.value = shuffleModeEnabled
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                }
                
                // Обработка ошибок плеера (например, файл не найден)
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    // Можно добавить логику уведомления пользователя
                }
            })
            startProgressUpdate()
        }, MoreExecutors.directExecutor())
    }

    private fun startProgressUpdate() {
        scope.launch {
            while (isActive) {
                mediaController?.let {
                    _currentPosition.longValue = it.currentPosition
                    _duration.longValue = it.duration.coerceAtLeast(0L)
                }
                delay(1000)
            }
        }
    }

    /**
     * Начать воспроизведение списка треков
     */
    fun playTrack(track: Track, playlist: List<Track>) {
        this.currentPlaylist = playlist // Сохраняем плейлист
        
        val mediaItems = playlist.map { t ->
            MediaItem.Builder()
                .setMediaId(t.id.toString())
                .setUri(t.uri)
                .setTag(t)
                .build()
        }
        val startIndex = playlist.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
            // Сразу обновляем текущий трек, не дожидаясь колбэка, для мгновенной реакции UI
            _currentTrack.value = track
        }
    }

    fun togglePlayPause() {
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
    }

    fun playNext() = mediaController?.seekToNext()
    fun playPrevious() = mediaController?.seekToPrevious()

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun toggleShuffle() {
        mediaController?.shuffleModeEnabled = !(mediaController?.shuffleModeEnabled ?: false)
    }

    fun toggleRepeatMode() {
        val nextMode = when (mediaController?.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        mediaController?.repeatMode = nextMode
    }

    fun release() {
        scope.cancel()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
