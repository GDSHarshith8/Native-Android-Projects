package com.taskmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.taskmaker.presentation.TMNavGraph
import com.taskmaker.presentation.screens.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun TaskMakerApp(content: @Composable (AppTheme, (AppTheme) -> Unit) -> Unit) {
    val selectedTheme = rememberSaveable { mutableStateOf(AppTheme.SYSTEM_DEFAULT) }

    val colorScheme = when (selectedTheme.value) {
        AppTheme.LIGHT -> lightColorScheme()
        AppTheme.DARK -> darkColorScheme()
        AppTheme.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        content(selectedTheme.value) { selectedTheme.value = it }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskMakerApp { selectedTheme, onThemeChange ->
                TMNavGraph(
                    selectedTheme = selectedTheme,
                    onThemeChange = onThemeChange
                )
            }
        }
    }
}
