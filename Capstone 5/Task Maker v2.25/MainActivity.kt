package com.composetrails

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.composetrails.Zprojects.until8.PU8NavGraph
import com.composetrails.Zprojects.until8.projectScreens.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val selectedTheme = rememberSaveable { mutableStateOf(AppTheme.SYSTEM_DEFAULT) }

            val colorScheme = when (selectedTheme.value) {
                AppTheme.LIGHT -> lightColorScheme()
                AppTheme.DARK -> darkColorScheme()
                AppTheme.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            }

            MaterialTheme(
                colorScheme = colorScheme
            ) {
                PU8NavGraph(
                    selectedTheme = selectedTheme.value,
                    onThemeChange = { selectedTheme.value = it }
                )
            }
        }
    }
}
