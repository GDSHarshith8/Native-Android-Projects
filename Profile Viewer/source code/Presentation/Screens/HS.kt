package com.profileviewer.Presentation.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.profileviewer.Presentation.ViewModel.ProfileViewModel
import com.profileviewer.Presentation.ViewModel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    viewModel: ProfileViewModel,
    onNavigate: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState
    var hasNavigated by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {

            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Success -> {
                if (hasNavigated) {
                    // ✅ We already went to Profile → now we're back
                    FilledTonalButton(
                        onClick = {
                            hasNavigated = false
                            viewModel.fetchProfile()
                        }
                    ) {
                        Text("Load Profile")
                    }
                } else {
                    // ✅ First time success → waiting to navigate
                    CircularProgressIndicator()
                }
            }

            is UiState.Idle,
            is UiState.Error -> {
                FilledTonalButton(
                    onClick = {
                        hasNavigated = false
                        viewModel.fetchProfile()
                    }
                ) {
                    Text("Load Profile")
                }
            }
        }
    }

    LaunchedEffect(uiState, hasNavigated) {
        if (uiState is UiState.Success && !hasNavigated) {
            hasNavigated = true
            onNavigate()
        }
        if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar("No internet connection")
        }
    }
}