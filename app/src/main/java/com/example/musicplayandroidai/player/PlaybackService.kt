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

        // Настраиваем аудио-атрибуты для музыки.
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val settingsManager = SettingsManager.getInstance(this)
        
        // Создаем ExoPlayer с ВКЛЮЧЕННОЙ обработкой фокуса по умолчанию.
        // Это обеспечит:
        // 1. Паузу при входящих и исходящих звонках.
        // 2. Паузу, когда другое приложение (например, YouTube) запрашивает фокус.
        // 3. Приглушение громкости при уведомлениях (Ducking).
        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // Всегда true для корректной работы системы
            .setHandleAudioBecomingNoisy(true) // Пауза при отключении наушников
            .build()

        // Слушаем изменения в настройках, если в будущем захотим менять тип фокуса
        settingsManager.isAudioFocusEnabled
            .onEach { isEnabled ->
                AppLogger.d("Audio focus preference: $isEnabled. Service currently ensures focus for calls.")
                // В данной реализации мы оставляем фокус включенным на уровне плеера, 
                // так как это критично для звонков.
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

        AppLogger.d("PlaybackService started with system Audio Focus handling.")
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
