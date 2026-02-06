package com.example.musicplayandroidai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.musicplayandroidai.player.PlayerManager
import com.example.musicplayandroidai.ui.AppRoot
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme

class MainActivity : ComponentActivity() {
    
    private var playerManager: PlayerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        playerManager = PlayerManager(this)

        setContent {
            MusicPlayAndroidAITheme {
                AppRoot(playerManager!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager?.release()
    }
}
