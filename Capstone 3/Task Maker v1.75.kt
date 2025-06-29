package com.composetrails.projects

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Serializable
data class Task(val id:  Int , val title : String , val isDone : Boolean)

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PU5() {
    var selectedTab by remember { mutableIntStateOf(-1) }

    val tasks = remember { mutableStateListOf<Task>() }

    val sbh = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // to launch snackbar

    val addedTask = remember { mutableStateOf<Task?>(null) }
    val deletedTask = remember { mutableStateOf<Task?>(null) }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var newTaskText by rememberSaveable { mutableStateOf("") }

    var nextId by rememberSaveable { mutableIntStateOf(1) }

    LaunchedEffect(showDialog) {
        if (!showDialog) {
            selectedTab = -1
        }
    }

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
                    val labels = listOf("Finished Tasks", "Unfinished Tasks", "Add Task", "Delete Task")
                    val icons = listOf(
                        Icons.Default.Check,
                        Icons.Default.Clear,
                        Icons.Default.Add,
                        Icons.Default.Remove
                    )

                    NavigationBar(
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxSize(),
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        labels.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = {
                                    when (index) {
                                        2 -> showDialog = true // Add Task
                                        3 -> selectedTab = 3   // Delete Mode
                                        else -> selectedTab = index
                                    }
                                },
                                icon = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = label
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )

                                    }
                                },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors()
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        val filteredTasks = when (selectedTab) {
            0 -> tasks.filter { it.isDone }
            1 -> tasks.filter { !it.isDone }
            else -> tasks
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // if empty shows + msg in center else starts from top
            verticalArrangement = if (filteredTasks.isEmpty()) Arrangement.Center else Arrangement.Top
        ) {
            if (filteredTasks.isEmpty()) {
                Text(
                    text = "no tasks at the moment!\n\n use + to add tasks!",
                    fontSize = 16.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            } else {
                Column {
                    filteredTasks.forEach { task ->
                        val isCompleted = task.isDone
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Checkbox(
                                    checked = task.isDone,
                                    onCheckedChange = if (selectedTab == 3) null else { isChecked ->
                                        val updatedTask = task.copy(isDone = isChecked)
                                        val index = tasks.indexOf(task)
                                        if (index != -1) {
                                            tasks[index] = updatedTask
                                        }
                                    }
                                )
                                Text(
                                    text = task.title,
                                    fontSize = 22.sp,
                                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    modifier = Modifier.weight(1f)
                                )
                                if (selectedTab == 3) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Delete Task",
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                deletedTask.value = task
                                                tasks.remove(task)
                                                scope.launch {
                                                    val result = sbh.showSnackbar(
                                                        message = "Task deleted",
                                                        actionLabel = "Undo",
                                                        withDismissAction = true,
                                                        duration = SnackbarDuration.Short
                                                    )
                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        deletedTask.value?.let {
                                                        /* If tasks have similar content, undoing after adding new tasks might misplace.
                                                        This tries to restore it at original index */
                                                            tasks.add(it.id - 1, it)
                                                            deletedTask.value = null
                                                        }
                                                    }
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Add New Task") },
                    text = {
                        TextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            placeholder = { Text("Enter task description") },
                            label = { Text("Task") }
                        )
                    },
                    confirmButton = {
                        Text(
                            "Add",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    if (newTaskText.isNotBlank()) {
                                        val taskToAdd = Task(
                                            id = nextId,
                                            title = newTaskText,
                                            isDone = false
                                        )
                                        addedTask.value = taskToAdd
                                        tasks.add(taskToAdd)
                                        nextId++

                                        newTaskText = ""
                                        showDialog = false

                                        // Toggle selectedTab to force recomposition:
                                        selectedTab = 0  // temporarily switch tab
                                        selectedTab = -1 // switch back to all tasks

                                        scope.launch {
                                            val result = sbh.showSnackbar(
                                                "Task added",
                                                actionLabel = "Undo",
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Short)

                                            if (result == SnackbarResult.ActionPerformed) {
                                                addedTask.value?.let {
                                                    tasks.remove(it)
                                                    addedTask.value = null
                                                }
                                            }
                                        }
                                    }
                                }
                        )
                    },
                    dismissButton = {
                        Text(
                            "Cancel",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    newTaskText = ""
                                    showDialog = false
                                }
                        )
                    }
                )
            }
        }
    }
}
