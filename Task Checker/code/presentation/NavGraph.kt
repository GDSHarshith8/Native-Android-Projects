package com.taskchecker.presentation

import com.taskchecker.presentation.screens.AppTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.taskchecker.presentation.screens.AddTasksScreen
import com.taskchecker.presentation.screens.AllTasksScreen
import com.taskchecker.presentation.screens.HMcontent
import com.taskchecker.presentation.viewModels.AddTasksVM
import com.taskchecker.presentation.viewModels.AllTasksVM
import com.taskchecker.presentation.viewModels.DeleteTasksVM
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// Step 1: Declare routes
object HomeRoute {
    const val route = "home"
}

object AddTaskRoute {
    const val route = "insertTask"
}

@Serializable
data class Task(val id: Int, val title: String, var isDone: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCNavGraph(
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onShowOnboarding: () -> Unit
) {
    val navController = rememberNavController()
    val sbh = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HMcontent(
                drawerState= drawerState,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeChange,
                onShowOnboarding = onShowOnboarding
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = sbh) },
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                ) {
                    TopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        title = {
                            Text(
                                text = "Task Checker",
                                fontSize = 16.sp,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.clickable {
                                    navController.navigate(HomeRoute.route) {
                                        launchSingleTop = true
                                        popUpTo(HomeRoute.route) { inclusive = true }
                                    }
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(AddTaskRoute.route) },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                        )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable(HomeRoute.route) {
                    // Fetch all the view models via Hilt
                    val allTasksVM: AllTasksVM = hiltViewModel()
                    val addTasksVM: AddTasksVM = hiltViewModel()
                    val deleteTasksVM: DeleteTasksVM = hiltViewModel()

                    // Pass all the view models to AllTasksScreen composable
                    AllTasksScreen(
                        allTasksViewModel = allTasksVM,
                        deleteTasksViewModel = deleteTasksVM,
                        padding = paddingValues
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
    }
}