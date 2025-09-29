package com.taskchecker.presentation.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskchecker.presentation.viewModels.AllTasksVM
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.taskchecker.db.Task
import com.taskchecker.presentation.viewModels.DeleteTasksVM
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.taskchecker.presentation.AddTaskRoute
import com.taskchecker.presentation.HomeRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTasksScreen(
    navController : NavController,
    allTasksViewModel: AllTasksVM = hiltViewModel(),
    deleteTasksViewModel: DeleteTasksVM = hiltViewModel(),
    initialFilter: TaskFilter = TaskFilter.All,
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onShowOnboarding: () -> Unit
) {
    val tasksFromDb by allTasksViewModel.tasks.collectAsStateWithLifecycle(emptyList())
    val selectedFilter = rememberSaveable { mutableStateOf(initialFilter) }

    val snackbarQueue = remember {
        MutableSharedFlow<Task>(extraBufferCapacity = Int.MAX_VALUE)
    }

    // State variable to temporarily hold the task marked for deletion
    val remUndo = remember { mutableStateOf<Task?>(null) }

    // 💡 NEW STATE: A key to force LazyColumn recomposition on undo
    var listRefreshKey by remember { mutableStateOf(0) }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(snackbarQueue) {
        // Collect tasks from the shared snackbar queue (1-by-1 in order of deletion)
        snackbarQueue.collect { task ->

            // Temporarily remove the task from visible UI (optimistic delete)
            remUndo.value = task

            // Show a snackbar asking the user if they want to undo the deletion
            showUndoDeleteSnackbar(
                sbh = snackBarHostState,
                task = task,
                message = "Task \"${task.title}\" deleted",

                // If user taps "Undo"
                onUndo = {
                    // Restore the task back into the database
                    allTasksViewModel.restoreTask(task)

                    // Show a follow-up snackbar confirming the restore
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Task \"${task.title}\" restored",
                            duration = SnackbarDuration.Short
                        )
                    }
                },

                // If user ignores or dismisses the snackbar
                onConfirmDelete = {
                    // Permanently delete the task from the database
                    deleteTasksViewModel.deleteTask(task)
                }
            )

            // Clear the temp task from UI state
            remUndo.value = null

            // Increment key to force LazyColumn recomposition (fixes visual glitches)
            listRefreshKey++
        }
    }

    val filteredTasks = tasksFromDb
        .filter { it.id != remUndo.value?.id }
        .filter {
            when (selectedFilter.value) {
                TaskFilter.Finished -> it.isDone
                TaskFilter.Unfinished -> !it.isDone
                TaskFilter.All -> true
            }
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HMcontent(
                drawerState = drawerState,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeChange,
                onShowOnboarding = onShowOnboarding
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
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
            AllTSContent(
                tasks = filteredTasks,
                isTaskListEmpty = tasksFromDb.isEmpty(),
                selectedFilter = selectedFilter.value,
                onFilterSelected = { selectedFilter.value = it },
                onToggleTaskDone = { allTasksViewModel.toggleTaskDone(it) },
                onTaskDelete = { task ->
                    scope.launch {
                        snackbarQueue.emit(task)
                    }
                },
                padding = paddingValues,
                snackBarHostState = snackBarHostState,
                listRefreshKey = listRefreshKey // Pass key down to force recomposition
            )
        }
    }
}

@Composable
fun AllTSContent(
    tasks: List<Task>,
    isTaskListEmpty: Boolean,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onToggleTaskDone: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    padding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    listRefreshKey: Int // ✅ Receive the recomposition key here
) {
    val listState = rememberLazyListState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(bottom = 80.dp, end = 16.dp) // leave space for FAB
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {

            // ✅ Always show filter row at the top
            FilterRow(
                selectedFilter = selectedFilter,
                filterOptions = FILTER_OPTIONS,
                onFilterSelected = onFilterSelected
            )

            Crossfade(
                targetState = selectedFilter,
                animationSpec  = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                ),
                label = "taskContentCrossfade"
            ) { filter ->
                when {
                    isTaskListEmpty -> {
                        CenteredEmptyMessage("No tasks at the moment!\n\nUse + to add a task")
                    }
                    tasks.isEmpty() && filter != TaskFilter.All -> {
                        val message = when (filter) {
                            TaskFilter.Finished -> "No completed tasks yet!\n\nComplete a task to see it here."
                            TaskFilter.Unfinished -> "You’re all caught up!\n\nNo unfinished tasks left."
                            else -> "No tasks"
                        }
                        CenteredEmptyMessage(message)
                    }
                    else -> {
                        TaskList(
                            tasks = tasks,
                            onToggleTaskDone = onToggleTaskDone,
                            onTaskDelete = onTaskDelete,
                            padding = innerPadding,
                            listRefreshKey = listRefreshKey,
                            listState = listState,
                            selectedFilter = filter,
                            onFilterSelected = onFilterSelected
                        )
                    }
                }
            }
        }
    }
}

enum class TaskFilter(val displayName: String) {
    All("All"),
    Finished("Finished"),
    Unfinished("Unfinished")
}

private val FILTER_OPTIONS = TaskFilter.values().toList()

@Composable
fun FilterRow(
    selectedFilter: TaskFilter,
    filterOptions: List<TaskFilter>,
    onFilterSelected: (TaskFilter) -> Unit
)
 {
    Surface(
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            filterOptions.forEach { option ->
                FilterChip(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    onClick = { onFilterSelected(option) },
                    label ={ Text(option.displayName) },
                    selected = selectedFilter == option,
                    leadingIcon = {
                        Icon(
                            imageVector = when (option) {
                                TaskFilter.All -> Icons.AutoMirrored.Filled.ViewList
                                TaskFilter.Finished -> Icons.Default.CheckCircle
                                TaskFilter.Unfinished -> Icons.Default.RemoveCircle
                            },
                            contentDescription = when (option) {
                                TaskFilter.All -> "All tasks"
                                TaskFilter.Finished -> "Finished tasks"
                                TaskFilter.Unfinished -> "Unfinished tasks"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleTaskDone: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    padding: PaddingValues,
    listRefreshKey: Int, // ✅ Receive key to force recomposition
    listState: LazyListState,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    val bottomOnlyPadding = PaddingValues(bottom = padding.calculateBottomPadding())

    // ✅ Correct fix: Wrap LazyColumn in key() to force recomposition
    key(listRefreshKey) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottomOnlyPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use `items` with a stable key to prevent rendering issues.
            items(
                items = tasks,
                key = { task -> task.id } // 💡 This is the key change!
            ) { task ->
                SwipeableListItem(
                    taskTitle = task.title,
                    taskIsDone = task.isDone,
                    onSwipeStartToEnd = { onToggleTaskDone(task) },
                    onSwipeEndToStart = { onTaskDelete(task) }
                )
            }
        }
    }
}
