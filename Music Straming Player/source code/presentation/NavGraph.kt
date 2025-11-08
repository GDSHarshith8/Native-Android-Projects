package com.musicplayer.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.musicplayer.datastore.UserPreferencesViewModel
import com.musicplayer.presentation.screens.AppTheme
import com.musicplayer.presentation.screens.HMcontent
import com.musicplayer.presentation.screens.HomeScreen
import com.musicplayer.presentation.screens.MusicBottomSheet // The fixed BottomSheet
import com.musicplayer.presentation.screens.SearchHostScreen
import com.musicplayer.presentation.viewModels.PlayerViewModel
import kotlinx.coroutines.launch

//@Composable
//fun MusicNavGraph2M(
//    userPreferencesViewModel: UserPreferencesViewModel,
//    playerViewModel: PlayerViewModel,
//    onThemeSelected: (AppTheme) -> Unit
//) {
//    val navController = rememberNavController()
//
//    // Bottom sheet wraps the entire navigation
//    MusicBottomSheet(playerViewModel = playerViewModel) { paddingValues ->
//        NavHost(
//            navController = navController,
//            startDestination = "home",
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues) // respect mini-player height
//        ) {
////            composable("home") {
////                HomeScreen(
////                    userPreferencesViewModel = userPreferencesViewModel,
////                    onThemeSelected = onThemeSelected,
////                    onNavigateToSearch = { navController.navigate("search") }
////                )
////            }
//            composable("search") {
//                SearchHostScreen(
//                    onThemeSelected = onThemeSelected,
//                    onNavigateBack = { navController.popBackStack() },
//                    playerViewModel = playerViewModel
//                )
//            }
//        }
//    }
//}

@Composable
fun MusicNavGraph(
    userPreferencesViewModel: UserPreferencesViewModel,
    playerViewModel: PlayerViewModel,
    onThemeSelected: (AppTheme) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Bottom sheet wraps the entire navigation
    MusicBottomSheet(playerViewModel = playerViewModel) { paddingValues ->

        // Single drawer at the root
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HMcontent(
                    userPreferencesViewModel = userPreferencesViewModel,
                    onThemeSelected = onThemeSelected,
                    drawerState = drawerState
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable("home") {
                    HomeScreen(
                        userPreferencesViewModel = userPreferencesViewModel,
                        onThemeSelected = onThemeSelected,
                        onNavigateToSearch = { navController.navigate("search") },
                        onMenuClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }
                    )
                }
                composable("search") {
                    SearchHostScreen(
                        playerViewModel = playerViewModel,
                        onThemeSelected = onThemeSelected,
                        onNavigateBack = { navController.popBackStack() },
                        onMenuClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }
                    )
                }
            }
        }
    }
}