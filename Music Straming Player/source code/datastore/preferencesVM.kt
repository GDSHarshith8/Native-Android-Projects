package com.musicplayer.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.presentation.screens.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val preferences:  MPPreferencesManager
) : ViewModel() {

    private val _selectedTheme = MutableStateFlow(AppTheme.SYSTEM_DEFAULT)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme

    init {
        viewModelScope.launch {
            preferences.appTheme.collect { theme ->
                _selectedTheme.value = theme
                println("UserPreferencesViewModel: theme updated -> $theme")
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferences.setAppTheme(theme)
            _selectedTheme.value = theme
        }
    }

}