package com.musicplayer.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.musicplayer.presentation.screens.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name ="LTPreferencesDataStore")

@Singleton
class MPPreferencesManager @Inject constructor(
    @ApplicationContext private val context : Context
){
    companion object {
        private val APP_THEME_KEY = stringPreferencesKey("app_theme")

        private val DEFAULT_APP_THEME = AppTheme.SYSTEM_DEFAULT
    }
    // Flow to observe theme preference as AppTheme enum
    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { prefs ->
            val themeName = prefs[APP_THEME_KEY] ?: DEFAULT_APP_THEME.name
            runCatching { AppTheme.valueOf(themeName) }.getOrDefault(DEFAULT_APP_THEME)
        }

    // Save selected theme as string
    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[APP_THEME_KEY] = theme.name
        }
    }

}