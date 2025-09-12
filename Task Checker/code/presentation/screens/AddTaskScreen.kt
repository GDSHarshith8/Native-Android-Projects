package com.taskchecker.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.taskchecker.presentation.viewModels.AddTasksVM
import com.taskchecker.presentation.viewModels.DeleteTasksVM
import kotlinx.coroutines.launch

@Composable
fun AddTasksScreenX(
    navController: NavController,
    sbh: SnackbarHostState,
    padding: PaddingValues,
    addTasksViewModel: AddTasksVM = hiltViewModel(),  // ViewModel for adding tasks
    deleteTasksViewModel: DeleteTasksVM = hiltViewModel()  // ViewModel for deleting tasks
) {
    var newTaskText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    TaskInputDialog(
        value = newTaskText,
        onValueChange = { newTaskText = it },
        onConfirm = {
            if (newTaskText.isNotBlank()) {
                // Create the task
                val taskToAdd = com.taskchecker.db.Task(
                    id = 0,
                    title = newTaskText,
                    isDone = false
                )

                // Navigate back immediately before launching the coroutine
                navController.popBackStack()

                scope.launch {
                    // Add the task using AddTasksVM
                    addTasksViewModel.addTask(taskToAdd)

                    // Show the snackbar with undo action
                    showUndoAddSnackbar(sbh, taskToAdd) {
                        // In case of undo, delete the task using DeleteTasksVM
                        deleteTasksViewModel.deleteTask(taskToAdd)
                    }
                }
            }
        },
        onDismiss = {
            // Navigate back if the user cancels the dialog
            navController.popBackStack()
        }
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTasksScreen(
    navController: NavController,
    sbh: SnackbarHostState,
    padding: PaddingValues,
    addTasksViewModel: AddTasksVM = hiltViewModel(),
    deleteTasksViewModel: DeleteTasksVM = hiltViewModel()
) {
    var newTaskText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // State for controlling the bottom sheet visibility
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        sheetState.show()  // Show bottom sheet when screen opens
    }

    ModalBottomSheet(
        onDismissRequest = {
            // Close the sheet and navigate back on dismiss
            navController.popBackStack()
        },
        sheetState = sheetState,
        dragHandle = {}, // 👈 removes the pill,
        scrimColor = Color.Black.copy(alpha = 0.0005f) // Lower alpha = lighter scrim
    ) {
        BottomSheetContent(
            value = newTaskText,
            onValueChange = { newTaskText = it },
            onConfirm = {
                if (newTaskText.isNotBlank()) {
                    val taskToAdd = com.taskchecker.db.Task(
                        id = 0,
                        title = newTaskText,
                        isDone = false
                    )

                    // Close bottom sheet first
                    scope.launch {
                        sheetState.hide()  // Wait for hide animation

                        // Optional small delay for smoothness
                        kotlinx.coroutines.delay(100)

                        navController.popBackStack()  // Then navigate away immediately

                        // Do DB stuff afterwards to avoid UI delay
                        addTasksViewModel.addTask(taskToAdd)

                        showUndoAddSnackbar(sbh, taskToAdd) {
                            deleteTasksViewModel.deleteTask(taskToAdd)
                        }
                    }
                }
            },
            onDismiss = {
                scope.launch {
                    sheetState.hide()  // Wait for hide animation
                    kotlinx.coroutines.delay(100)  // Small delay for smoothness
                    navController.popBackStack()  // Then navigate back
                }
            }
        )
    }
}


@Composable
fun BottomSheetContent(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Add New Task", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Enter task description") },
            label = { Text("Task") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onConfirm) {
                Text("Add")
            }
        }
    }
}