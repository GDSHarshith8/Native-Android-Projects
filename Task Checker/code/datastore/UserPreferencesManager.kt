package com.taskchecker.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.taskchecker.presentation.screens.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for DataStore instance
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
        private val APP_THEME_KEY = stringPreferencesKey("app_theme")

        private const val DEFAULT_IS_FIRST_TIME = true
        private val DEFAULT_APP_THEME = AppTheme.SYSTEM_DEFAULT
    }

    // Flow to observe onboarding state
    val isFirstTime: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[IS_FIRST_TIME_KEY] ?: DEFAULT_IS_FIRST_TIME }

    // Flow to observe theme preference as AppTheme enum
    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { prefs ->
            val themeName = prefs[APP_THEME_KEY] ?: DEFAULT_APP_THEME.name
            runCatching { AppTheme.valueOf(themeName) }.getOrDefault(DEFAULT_APP_THEME)
        }

    // Mark onboarding as completed
    suspend fun setFirstTimeDone() {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_TIME_KEY] = false
        }
    }

    // Save selected theme as string
    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[APP_THEME_KEY] = theme.name
        }
    }
}
