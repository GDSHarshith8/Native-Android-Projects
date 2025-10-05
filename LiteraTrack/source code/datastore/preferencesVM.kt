package com.literatrack.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literatrack.presentation.utils.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
    private val preferences:  LTPreferencesManager
) : ViewModel() {

    private val _isFirstTime = MutableStateFlow<Boolean?>(null)
    val isFirstTime: StateFlow<Boolean?> = _isFirstTime

    private val _selectedTheme = MutableStateFlow(AppTheme.SYSTEM_DEFAULT)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme

    init {
        viewModelScope.launch {
            _selectedTheme.value = preferences.appTheme.first()
            _isFirstTime.value = preferences.isFirstTime.first()
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferences.setAppTheme(theme)
            _selectedTheme.value = theme
        }
    }

    fun setFirstTimeDone() {
        viewModelScope.launch {
            preferences.setFirstTimeDone()
            _isFirstTime.value = false
        }
    }
}