package com.taskchecker.presentation

import com.taskchecker.presentation.screens.AppTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.taskchecker.presentation.screens.AddTasksScreen
import com.taskchecker.presentation.screens.AllTasksScreen
import com.taskchecker.presentation.viewModels.AddTasksVM
import com.taskchecker.presentation.viewModels.AllTasksVM
import com.taskchecker.presentation.viewModels.DeleteTasksVM

// Step 1: Declare routes
object HomeRoute {
    const val route = "home"
}

object AddTaskRoute {
    const val route = "insertTask"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCNavGraph(
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onShowOnboarding: () -> Unit
) {
    val navController = rememberNavController()
    val sbh = remember { SnackbarHostState() }
    NavHost(
        navController = navController,
        startDestination = HomeRoute.route,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(HomeRoute.route) {
            // Fetch all the view models via Hilt
            val allTasksVM: AllTasksVM = hiltViewModel()
            val addTasksVM: AddTasksVM = hiltViewModel()
            val deleteTasksVM: DeleteTasksVM = hiltViewModel()

            AllTasksScreen(
                navController = navController,
                allTasksViewModel = allTasksVM,
                deleteTasksViewModel = deleteTasksVM,
                selectedTheme = selectedTheme,
                onThemeChange = onThemeChange,
                onShowOnboarding = onShowOnboarding
            )
        }
        // Dialog destination
        dialog(AddTaskRoute.route) {
            val addTasksVM: AddTasksVM = hiltViewModel()
            val deleteTasksVM: DeleteTasksVM = hiltViewModel()

            AddTasksScreen(
                navController = navController,
                sbh = sbh,
                padding = PaddingValues(0.dp), // dialogs usually don't need insets
                addTasksViewModel = addTasksVM,
                deleteTasksViewModel = deleteTasksVM
            )
        }
    }
}
