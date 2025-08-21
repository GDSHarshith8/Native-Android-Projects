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
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.serialization.Serializable

@Serializable
data class Task(val id:  Int , val title : String , val isDone : Boolean)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PU4() {
    var selectedTab by remember { mutableIntStateOf(-1) }

    var tasks = remember { mutableStateListOf<Task>() }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var newTaskText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        IconButton(onClick = { /* handle menu click */ }) {
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
                        .fillMaxWidth(0.9f) // width less than full to show it floats
                        .height(100.dp)
                        .navigationBarsPadding() // <- important
                        .padding(bottom = 16.dp), // makes it float up
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    var labels =
                        listOf("Finished Taks", "Unfinished Taks", "Add Task", "Delete Task")
                    var icons = listOf(
                        Icons.Default.Check,
                        Icons.Default.Clear,
                        Icons.Default.Add,
                        Icons.Default.Remove
                    )

                    NavigationBar(
                        tonalElevation = 0.dp, // zero because Card gives elevation
                        modifier = Modifier.fillMaxSize(),
                        windowInsets = WindowInsets(0, 0, 0, 0) // remove default padding
                    ) {
                        //add remove tasks here!
                        labels.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        Icon(
                                            imageVector = icons[index],
                                            contentDescription = labels[index]
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = labels[index],
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
        // Handle side effects first
        when (selectedTab) {
            2 -> {
                showDialog = true
                selectedTab = -1 // Reset tab to prevent repeated triggering
            }
        }

        // Compute filtered task list
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
            verticalArrangement = Arrangement.SpaceEvenly
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = task.isDone, // task is lambd vn
                                onCheckedChange = { isChecked ->
                                    val updatedTask = task.copy(isDone = isChecked)
                                    val index = tasks.indexOf(task)
                                    if (index != -1) {
                                        tasks[index] = updatedTask
                                    }
                                }
                            )
                            Text(
                                text = task.title,
                                fontSize = 20.sp,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )

                            // Show delete icon only when selectedTab == 3 (Delete Mode)
                            if (selectedTab == 3) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Delete Task",
                                    modifier = Modifier
                                        .clickable {
                                            tasks.remove(task)
                                        }
                                        .padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Add Task Dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Add New Task") },
                    text = {
                        TextField(
                            value = newTaskText,
                            onValueChange = { newTaskText = it },
                            placeholder = { Text("Enter task description") }
                        )
                    },
                    confirmButton = {
                        Text(
                            "Add",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    if (newTaskText.isNotBlank()) {
                                        val newId = tasks.size + 1
                                        tasks.add(
                                            Task(
                                                id = newId,
                                                title = newTaskText,
                                                isDone = false
                                            )
                                        )
                                        newTaskText = ""
                                        showDialog = false
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

@Preview
@Composable
fun P4P(){
    PU4()
}

