package com.literatrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.literatrack.datastore.UserPreferencesViewModel
import com.literatrack.presentation.navigation.LTnavgraph
import com.literatrack.presentation.utils.OnboardingScreen
import com.literatrack.ui.theme.LiteraTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferencesVM: UserPreferencesViewModel = hiltViewModel()
            val selectedTheme by userPreferencesVM.selectedTheme.collectAsState()
            val isFirstTime by userPreferencesVM.isFirstTime.collectAsState()

            LiteraTrackTheme(selectedTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isFirstTime == null) {
                        // Show loading UI while data loads
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (isFirstTime == true) {
                            val onboardingPages = listOf(
                                "📚\n\nWelcome to Literatrack \n\n your simple way to track\nthe books you’re reading.",
                                "➕\n\n Find your next great read\n and add it to your wishlist.",
                                "📴\n\nEverything works offline.\n\n Your list is always with you,\nno sync needed.",
                                "🗑️\n\nChanged your mind?\n\n Long-press a book to remove \nit from your list.",
                                "✅\n\nTrack your progress\nwith a single swipe,\nfrom 'Reading' to 'Completed'."
                            )
                            OnboardingScreen(
                                obPages = onboardingPages,
                                onFinish = { userPreferencesVM.setFirstTimeDone() }
                            )
                        } else {
                            val navController = rememberNavController()

                            LTnavgraph(
                                navController = navController,
                                selectedTheme = selectedTheme,
                                onThemeChange = { newTheme -> userPreferencesVM.setTheme(newTheme) },
                                userPreferencesVM = userPreferencesVM
                            )
                        }
                    }
                }
            }
        }
    }
}