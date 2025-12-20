package com.profileviewer.Presentation.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.profileviewer.Presentation.Screens.HomeContent
import com.profileviewer.Presentation.Screens.ProfileContent
import com.profileviewer.Presentation.ViewModel.ProfileViewModel
import com.profileviewer.Presentation.ViewModel.UiState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun PVNavGraph(viewModel: ProfileViewModel) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                Routes.HOME -> {
                    TopAppBar(
                        title = { Text("Profile Viewer") },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Settings, null)
                            }
                        }
                    )
                }

                Routes.PROFILE -> {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    null
                                )
                            }
                        },
                        title = {
                            if (uiState is UiState.Success) {
                                Text((uiState as UiState.Success).user.username)
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Settings, null)
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {

            composable(Routes.HOME) {
                HomeContent(
                    viewModel = viewModel,
                    onNavigate = { navController.navigate(Routes.PROFILE) },
                    snackbarHostState = snackbarHostState
                )
            }

            composable(Routes.PROFILE) {
                ProfileContent(viewModel = viewModel)
            }
        }
    }
}