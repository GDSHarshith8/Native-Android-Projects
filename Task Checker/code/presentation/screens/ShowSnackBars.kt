package com.taskchecker.presentation.screens

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.taskchecker.db.Task

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

suspend fun showUndoDeleteSnackbar(
    sbh: SnackbarHostState,
    task: Task,
    message: String = "Task deleted",  // You can override this with a custom message
    onUndo: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val result = sbh.showSnackbar(
        message = message,
        actionLabel = "Undo",
        duration = SnackbarDuration.Short
    )

    if (result == SnackbarResult.ActionPerformed) {
        onUndo()
    } else {
        onConfirmDelete()
    }
}