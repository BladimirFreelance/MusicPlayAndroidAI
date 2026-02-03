package com.example.musicplayandroidai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.musicplayandroidai.ui.AppRoot
import com.example.musicplayandroidai.ui.theme.MusicPlayAndroidAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayAndroidAITheme {
                AppRoot()
            }
        }
    }
}
