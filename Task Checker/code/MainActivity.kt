package com.taskchecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskchecker.datastore.UserPreferencesViewModel
import com.taskchecker.presentation.OnboardingScreen
import com.taskchecker.presentation.TCNavGraph
import com.taskchecker.presentation.screens.AppTheme
import com.taskchecker.ui.theme.TaskCheckerThemeWrapper
import dagger.hilt.android.AndroidEntryPoint
import com.taskchecker.ui.theme.Typography

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge drawing for better UI immersion

        // Make content draw behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Get UserPreferencesViewModel via Hilt (handles DataStore access & state)
            val userPreferencesVM: UserPreferencesViewModel = hiltViewModel()

            // Collect current selected theme from ViewModel as Compose State
            val selectedTheme by userPreferencesVM.selectedTheme.collectAsState()
            // Collect onboarding completion status from ViewModel (nullable Boolean)
            val isFirstTime by userPreferencesVM.isFirstTime.collectAsState()

            // Local state to control showing onboarding screen on demand
            val showOnboarding = remember { mutableStateOf(false) }

            // Show a loading spinner while onboarding status is being loaded
            if (isFirstTime == null) {
                androidx.compose.material3.Surface {
                    androidx.compose.material3.CircularProgressIndicator()
                }
                return@setContent
            }

            // Wrap entire app content in your custom theme based on selectedTheme
            TaskCheckerThemeWrapper(
                selectedTheme = selectedTheme,
                onThemeChange = { newTheme ->
                    // When theme changes, tell ViewModel to update DataStore and state
                    userPreferencesVM.setTheme(newTheme)
                }
            ) {
                when {
                    // Show onboarding if it's the first launch or if triggered manually
                    isFirstTime == true || showOnboarding.value -> {
                        OnboardingScreen(
                            onFinish = {
                                // When onboarding is done, update DataStore and state
                                userPreferencesVM.setFirstTimeDone()
                                showOnboarding.value = false // Hide onboarding
                            }
                        )
                    }
                    else -> {
                        // Otherwise show the main app navigation graph
                        TCNavGraph(
                            selectedTheme = selectedTheme,
                            onThemeChange = { newTheme ->
                                // Pass theme change handler to navigation graph & drawer
                                userPreferencesVM.setTheme(newTheme)
                            },
                            onShowOnboarding = {
                                // Show onboarding screen when requested from drawer menu
                                showOnboarding.value = true
                            }
                        )
                    }
                }
            }
        }
    }
}