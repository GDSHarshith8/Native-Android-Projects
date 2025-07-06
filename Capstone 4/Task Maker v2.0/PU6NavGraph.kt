package com.composetrails.projects.until6

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.composetrails.projects.until6.projectScreens.AddTS
import com.composetrails.projects.until6.projectScreens.AllTS
import com.composetrails.projects.until6.projectScreens.DelTS
import com.composetrails.projects.until6.projectScreens.FinTS
import com.composetrails.projects.until6.projectScreens.UnFinTS
import kotlinx.serialization.Serializable


// step 1: declare routes using a sealed class

object HomeRoute {
    const val route = "home"
}

sealed class BottomNavRoutes(val label: String, val icon: ImageVector, val route: String) {
    object Finished : BottomNavRoutes("Finished", Icons.Default.Check, "finished")
    object Unfinished : BottomNavRoutes("Unfinished", Icons.Default.Clear, "unfinished")
    object Add : BottomNavRoutes("Add", Icons.Default.Add, "add")
    object Delete : BottomNavRoutes("Delete", Icons.Default.Remove, "delete")

    companion object{
        val items=listOf( Finished, Unfinished,Add,Delete)
    }
}

// step 2 : create individual screens

//  step 3: Create NavGraph using sealed class and NavigationBar

@Serializable
data class Task(val id:  Int, val title : String, var isDone : Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PU6NavGraph(){

    val navController = rememberNavController()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val sbh = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // to launch snackbar

    val tasks = remember { mutableStateListOf<Task>() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = sbh) },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Task Checker",
                            fontSize = 16.sp,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
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
        bottomBar = {
            PU6BAB(navController,currentRoute)
        }
        ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute.route,  // ✅ set home as start
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(HomeRoute.route) { AllTS(tasks,paddingValues) }

            composable(BottomNavRoutes.Finished.route) { FinTS(tasks, paddingValues) }
            composable(BottomNavRoutes.Unfinished.route) { UnFinTS(tasks, paddingValues) }
            composable(BottomNavRoutes.Add.route) { AddTS(navController,tasks, sbh, paddingValues) }
            composable(BottomNavRoutes.Delete.route) { DelTS(tasks, sbh, paddingValues) }

        }

    }

}

// step 4 : create BottomAppBar using AppScreens sealed class
@Composable
fun PU6BAB(navController: NavController,currentRoute: String?){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(100.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            BottomAppBar {
                BottomNavRoutes.items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },

                        selected = currentRoute?.startsWith(screen.route.substringBefore("?")) == true,
                        onClick = {
                            val route = screen.route

                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


// step 5 : call PU6NavGraph() within setContent of MainActivity */
