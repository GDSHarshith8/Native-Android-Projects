import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PU2() {
    var tasks = remember { mutableStateListOf<String>() }
    var completedT = remember { mutableStateListOf<String>() }

    var showDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }

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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                shape = RectangleShape,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task"
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // spacing between icon and text
                    Text("New Task")
                }
            )

        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.safeContent,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            if (tasks.isEmpty())
                Text(
                    text = "no tasks at the moment!\n\n use + to add tasks!", fontSize = 16.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            else {
                Column {
                    tasks.forEach { task ->
                        val isCompleted = task in completedT
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isCompleted,
                                onCheckedChange = {
                                    if (it)
                                        completedT.add(task)
                                    else
                                        completedT.remove(task)
                                },
                            )
                            Text(
                                text = task,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }

            /*
            show dialog to add new tasks
            this is AI generated part!
            although my project is to make hardcoded tasks but decided to make them dyanmic!
            Dialogs with be in user interaction section in my road map
            */
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
                                        tasks.add(newTaskText)
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
fun P2P(){
    PU2()
}

