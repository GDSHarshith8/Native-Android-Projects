package com.taskchecker.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableListItem(
    taskTitle: String,
    taskIsDone: Boolean, // Task completion status
    onSwipeStartToEnd: () -> Unit, // Left to Right (Mark as Finished or Revert)
    onSwipeEndToStart: () -> Unit  // Right to Left (Delete)
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onSwipeStartToEnd() // Toggle done/undone
                    false // Prevent the dismiss — keep the item visible
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // Allow dismiss — remove the item
                    true
                }
                else -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.5f }
    )

    // Show the item only while it's not fully dismissed
    if (dismissState.currentValue != SwipeToDismissBoxValue.EndToStart) {
        // SwipeToDismissBox state change logic
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            modifier = Modifier.padding(10.dp),
            backgroundContent = {
                SwipeBackground(dismissState, taskIsDone)
            }
        ) {
            TaskCardContent(taskTitle, taskIsDone)
        }
    } else {
        // Once dismissed, perform the deletion side effect
        LaunchedEffect(Unit) {
            onSwipeEndToStart()
        }
    }
}

@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState, taskIsDone: Boolean) {
    val direction = dismissState.dismissDirection

    val icon = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> {
            if (taskIsDone) Icons.Default.Close else Icons.Default.CheckCircle
        }
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
        else -> null
    }

    val color = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> if (taskIsDone) Color(0xFFEF5350) else Color(0xFF1CC71C)
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF0000)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        ) {
            // Only show the icon if the swipe is in progress
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TaskCardContent(taskTitle: String, taskIsDone: Boolean) {
    // Card for task content
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = taskTitle,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (taskIsDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
            // Add line-through effect if task is finished
            textDecoration = if (taskIsDone) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}
