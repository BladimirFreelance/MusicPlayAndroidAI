package com.example.musicplayandroidai.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerManager(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    fun prepare(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play() = player.play()

    fun pause() = player.pause()

    fun stop() = player.stop()

    fun release() = player.release()
}

