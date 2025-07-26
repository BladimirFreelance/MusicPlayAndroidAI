package com.example.musicplayandroidai.player

import android.content.Context
import android.net.Uri
import com.example.musicplayandroidai.AppLogger
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerManager(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    init {
        AppLogger.d("Player initialized")
    }

    fun prepare(uri: Uri) {
        try {
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            AppLogger.d("Player prepared with uri: $uri")
        } catch (e: Exception) {
            AppLogger.e("Error preparing player", e)
        }
    }

    fun play() {
        try {
            player.play()
            AppLogger.d("Playing audio")
        } catch (e: Exception) {
            AppLogger.e("Error during playback", e)
        }
    }

    fun pause() {
        try {
            player.pause()
            AppLogger.d("Paused audio")
        } catch (e: Exception) {
            AppLogger.e("Error pausing playback", e)
        }
    }

    fun stop() {
        try {
            player.stop()
            AppLogger.d("Stopped audio")
        } catch (e: Exception) {
            AppLogger.e("Error stopping playback", e)
        }
    }

    fun release() {
        player.release()
        AppLogger.d("Player released")
    }
}

