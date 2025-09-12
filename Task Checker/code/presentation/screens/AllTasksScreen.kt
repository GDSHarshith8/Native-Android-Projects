package com.taskchecker.presentation.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle


@Composable
fun AllTasksScreen(
    allTasksViewModel: AllTasksVM = hiltViewModel(),
    deleteTasksViewModel: DeleteTasksVM = hiltViewModel(),
    padding: PaddingValues,
    initialFilter: String = "All"
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
                "Finished" -> it.isDone
                "Unfinished" -> !it.isDone
                "All" -> true
                else -> true
            }
        }

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
        padding = padding,
        snackBarHostState = snackBarHostState,
        listRefreshKey = listRefreshKey // Pass key down to force recomposition
    )
}

@Composable
fun AllTSContent(
    tasks: List<Task>,
    isTaskListEmpty: Boolean,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
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
                    // Show EmptyTaskSurface below filter if needed
                    isTaskListEmpty -> {
                        CenteredEmptyMessage("No tasks at the moment!\n\nUse + to add a task")
                    }

                    tasks.isEmpty() && filter != "All" -> {
                        val message = when (filter) {
                            "Finished" -> "No completed tasks yet!\n\nComplete a task to see it here."
                            "Unfinished" -> "You’re all caught up!\n\nNo unfinished tasks left."
                            else -> "No tasks"
                        }
                        CenteredEmptyMessage(message)
                    }

                    else -> {
                        // Show tasks in a LazyColumn
                        TaskList(
                            tasks = tasks,
                            onToggleTaskDone = onToggleTaskDone,
                            onTaskDelete = onTaskDelete,
                            padding = innerPadding,
                            listRefreshKey = listRefreshKey,// Pass key down to TaskList
                            listState =  listState,
                            selectedFilter = selectedFilter,
                            onFilterSelected = onFilterSelected
                        )
                    }
                }
            }
        }
    }
}

private val FILTER_OPTIONS = listOf("All", "Finished", "Unfinished")

@Composable
fun FilterRow(
    selectedFilter: String,
    filterOptions: List<String>,
    onFilterSelected: (String) -> Unit
) {
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
                    label = { Text(option) },
                    selected = selectedFilter == option,
                    leadingIcon = {
                        Icon(
                            imageVector = when (option) {
                                "All" -> Icons.AutoMirrored.Filled.ViewList
                                "Finished" -> Icons.Default.CheckCircle
                                "Unfinished" -> Icons.Default.RemoveCircle
                                else -> Icons.AutoMirrored.Filled.Label
                            },
                            contentDescription = when (option) {
                                "All" -> "All tasks"
                                "Finished" -> "Finished tasks"
                                "Unfinished" -> "Unfinished tasks"
                                else -> "Filter icon"
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
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
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