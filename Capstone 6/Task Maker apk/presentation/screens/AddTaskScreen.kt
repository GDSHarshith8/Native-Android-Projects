package com.taskmaker.presentation.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.taskmaker.presentation.Task
import com.taskmaker.presentation.viewModels.AllTasksVM
import kotlinx.coroutines.launch

@Composable
fun AddTS(
    navController: NavController,
    sbh: SnackbarHostState,
    padding: PaddingValues
) {
    val viewModel = hiltViewModel<AllTasksVM>()
    var showDialog by remember { mutableStateOf(true) }
    var newTaskText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (showDialog) {
        TaskInputDialog(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            onConfirm = {
                if (newTaskText.isNotBlank()) {
                    val taskToAdd = Task(
                        id = 0, // Let Room auto-generate ID if set up that way
                        title = newTaskText,
                        isDone = false
                    )

                    // 🔥 Insert into Room via ViewModel
                    scope.launch {
                        viewModel.insertTask(taskToAdd)

                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }

                        showUndoAddSnackbar(sbh, taskToAdd) {
                            scope.launch {
                                viewModel.deleteTask(taskToAdd)
                            }
                        }
                    }

                    // Clear input and close dialog
                    newTaskText = ""
                    showDialog = false
                }
            },
            onDismiss = {
                newTaskText = ""
                showDialog = false
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}




@Composable
fun TaskInputDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Enter task description") },
                label = { Text("Task") }
            )
        },
        confirmButton = {
            Text(
                "Add",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = onConfirm)
            )
        },
        dismissButton = {
            Text(
                "Cancel",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = onDismiss)
            )
        }
    )
}

suspend fun showUndoAddSnackbar(
    sbh: SnackbarHostState,
    task: Task,
    onUndo: () -> Unit
) {
    val result = sbh.showSnackbar(
        message = "Task added",
        actionLabel = "Undo",
        withDismissAction = true,
        duration = SnackbarDuration.Short
    )

    if (result == SnackbarResult.ActionPerformed) {
        onUndo()
    }
}

