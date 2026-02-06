package com.example.musicplayandroidai.player

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicplayandroidai.AppLogger
import com.example.musicplayandroidai.MainActivity
import com.example.musicplayandroidai.data.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var exoPlayer: ExoPlayer

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val settingsManager = SettingsManager.getInstance(this)
        
        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, settingsManager.isAudioFocusEnabled.value)
            .setHandleAudioBecomingNoisy(true)
            .build()

        // Слушаем изменения настроек и обновляем поведение плеера
        settingsManager.isAudioFocusEnabled
            .onEach { isEnabled ->
                AppLogger.d("Audio focus management changed to: $isEnabled")
                exoPlayer.setAudioAttributes(audioAttributes, isEnabled)
            }
            .launchIn(serviceScope)

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(object : MediaSession.Callback {})
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        AppLogger.d("PlaybackService created with MediaSession")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null) {
            if (!player.playWhenReady || player.mediaItemCount == 0) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        AppLogger.d("PlaybackService destroyed")
        super.onDestroy()
    }
}
