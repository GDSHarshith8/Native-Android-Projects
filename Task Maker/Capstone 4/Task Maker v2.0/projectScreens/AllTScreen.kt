package com.composetrails.projects.until6.projectScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composetrails.projects.until6.Task


@Composable
fun AllTS(tasks: MutableList<Task>, padding: PaddingValues) {
    if (tasks.isEmpty()) {
        EmptyTaskSurface("No tasks at the moment!\n\nUse + to add a task",padding)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), // ✅ Apply scaffold padding here
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(tasks.size) { index ->
                val task = tasks[index]
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
                            onCheckedChange = { isChecked ->
                                tasks[index] = task.copy(isDone = isChecked)
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = task.title,
                            fontSize = 22.sp,
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}
