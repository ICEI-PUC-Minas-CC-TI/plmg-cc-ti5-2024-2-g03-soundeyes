package com.jerson.soundeyes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.jerson.soundeyes.presentation.navGraph.NavGraph
import com.jerson.soundeyes.utils.TextToSpeechManager
import com.jerson.soundeyes.ui.theme.SoundEyesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TextToSpeechManager.initialize(this)
        setContent {
            SoundEyesTheme {
                val navHostController = rememberNavController()
                NavGraph(navHostController)
            }
        }
    }
}



