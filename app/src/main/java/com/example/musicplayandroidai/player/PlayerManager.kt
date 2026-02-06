package com.example.musicplayandroidai.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayandroidai.AppLogger
import com.example.musicplayandroidai.data.PlaybackPersistenceManager
import com.example.musicplayandroidai.data.TracksRepository
import com.example.musicplayandroidai.ui.screens.library.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerManager(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val persistenceManager = PlaybackPersistenceManager(context)
    private val repository = TracksRepository(context)
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var positionSavingJob: Job? = null

    private val _currentTrack = mutableStateOf<Track?>(null)
    val currentTrack: State<Track?> = _currentTrack

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private var currentQueue = mutableListOf<Track>()

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                setupController()
                restorePlaybackState()
            } catch (e: Exception) {
                AppLogger.e("Error connecting MediaController", e)
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupController() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    startPositionSaving()
                } else {
                    stopPositionSaving()
                    saveState()
                }
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let {
                    val trackId = it.mediaId.toLongOrNull()
                    if (trackId != _currentTrack.value?.id) {
                        updateCurrentTrackFromMediaItem(it)
                    }
                }
                saveState()
            }
        })
    }

    private fun updateCurrentTrackFromMediaItem(mediaItem: MediaItem) {
        val metadata = mediaItem.mediaMetadata
        _currentTrack.value = Track(
            id = mediaItem.mediaId.toLongOrNull() ?: 0L,
            title = metadata.title?.toString() ?: "Unknown",
            artist = metadata.artist?.toString() ?: "Unknown Artist",
            duration = 0L,
            uri = mediaItem.localConfiguration?.uri ?: Uri.EMPTY,
            albumArtUri = metadata.artworkUri
        )
    }

    private fun restorePlaybackState() {
        scope.launch {
            val lastTrackId = persistenceManager.lastTrackId.first()
            val lastPosition = persistenceManager.lastPosition.first()
            val lastQueueIds = persistenceManager.lastQueue.first()

            if (lastQueueIds.isNotEmpty()) {
                val allTracks = repository.getAllTracks()
                currentQueue = lastQueueIds.mapNotNull { id -> allTracks.find { it.id == id } }.toMutableList()
                
                if (currentQueue.isNotEmpty()) {
                    val mediaItems = currentQueue.map { createMediaItem(it) }
                    mediaController?.setMediaItems(mediaItems)
                    
                    val trackIndex = if (lastTrackId != null) {
                        currentQueue.indexOfFirst { it.id == lastTrackId }.coerceAtLeast(0)
                    } else 0
                    
                    mediaController?.seekTo(trackIndex, lastPosition)
                    mediaController?.prepare()
                    
                    if (trackIndex < currentQueue.size) {
                        _currentTrack.value = currentQueue[trackIndex]
                    }
                }
            } else if (lastTrackId != null) {
                repository.getTrackById(lastTrackId)?.let { track ->
                    _currentTrack.value = track
                    mediaController?.setMediaItem(createMediaItem(track), lastPosition)
                    mediaController?.prepare()
                }
            }
        }
    }

    private fun createMediaItem(track: Track): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setArtworkUri(track.albumArtUri)
            .build()

        return MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.uri)
            .setMediaMetadata(metadata)
            .build()
    }

    fun playTrack(track: Track, playlist: List<Track> = emptyList()) {
        val controller = mediaController ?: return
        try {
            currentQueue = if (playlist.isNotEmpty()) playlist.toMutableList() else mutableListOf(track)
            val mediaItems = currentQueue.map { createMediaItem(it) }
            
            val startIndex = currentQueue.indexOf(track).coerceAtLeast(0)
            
            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.prepare()
            controller.play()
            _currentTrack.value = track
            saveState()
        } catch (e: Exception) {
            AppLogger.e("Error playing track", e)
        }
    }

    private fun saveState() {
        val controller = mediaController ?: return
        val currentId = _currentTrack.value?.id
        val position = controller.currentPosition
        val queueIds = currentQueue.map { it.id }
        
        scope.launch {
            persistenceManager.savePlaybackState(currentId, position, queueIds)
        }
    }

    private fun startPositionSaving() {
        positionSavingJob?.cancel()
        positionSavingJob = scope.launch {
            while (true) {
                delay(5000) // Сохраняем каждые 5 секунд
                saveState()
            }
        }
    }

    private fun stopPositionSaving() {
        positionSavingJob?.cancel()
        positionSavingJob = null
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    fun playNext() {
        mediaController?.seekToNext()
    }

    fun playPrevious() {
        mediaController?.seekToPrevious()
    }

    fun stop() {
        mediaController?.stop()
        _currentTrack.value = null
        stopPositionSaving()
        saveState()
    }

    fun release() {
        stopPositionSaving()
        saveState()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        mediaController = null
    }
}
