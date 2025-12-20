package com.profileviewer.Presentation.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.profileviewer.Repo.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState = _uiState

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = repository.fetchUser()
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("No internet connection")
            }
        }
    }
}
