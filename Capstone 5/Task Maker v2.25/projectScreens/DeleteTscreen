package com.composetrails.Zprojects.until8.projectScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composetrails.Zprojects.until8.Task
import kotlinx.coroutines.launch

@Composable
fun DelTS(tasks: MutableList<Task>, sbh: SnackbarHostState, padding: PaddingValues) {
    val deletedTask = remember { mutableStateOf<Task?>(null) }
    val scope = rememberCoroutineScope()

    if (tasks.isEmpty()) {
        EmptyTaskSurface("No tasks at the moment!\n\nUse + to add a task",padding)
    }else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            items(tasks.size) { index ->
                val task = tasks[index]
                DeleteTaskCard(task) {
                    deletedTask.value = task
                    tasks.remove(task)

                    scope.launch {
                        showUndoDeleteSnackbar(sbh, task) {
                            tasks.add(task)
                            deletedTask.value = null
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTaskCard(task: Task, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Delete Task",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

suspend fun showUndoDeleteSnackbar(
    sbh: SnackbarHostState,
    task: Task,
    onUndo: () -> Unit
) {
    val result = sbh.showSnackbar(
        message = "Task deleted",
        actionLabel = "Undo",
        withDismissAction = true,
        duration = SnackbarDuration.Short
    )

    if (result == SnackbarResult.ActionPerformed) {
        onUndo()
    }
}
