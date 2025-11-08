package com.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.musicplayer.datastore.UserPreferencesViewModel
import com.musicplayer.presentation.screens.AppTheme
import com.musicplayer.presentation.MusicNavGraph
import com.musicplayer.presentation.viewModels.PlayerViewModel
import com.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPreferencesViewModel: UserPreferencesViewModel = hiltViewModel()
            val selectedTheme by userPreferencesViewModel.selectedTheme.collectAsState()
            val playerVM : PlayerViewModel = hiltViewModel()

            // Determine dark/light mode
            val isDarkTheme = when (selectedTheme) {
                AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
            }

            // Apply theme dynamically
            MusicPlayerTheme(darkTheme = isDarkTheme) {
                MusicNavGraph(
                    onThemeSelected = { theme ->
                        userPreferencesViewModel.setTheme(theme) // propagate down
                    },
                    userPreferencesViewModel = userPreferencesViewModel,
                    playerViewModel = playerVM
                )
            }
        }
    }
}