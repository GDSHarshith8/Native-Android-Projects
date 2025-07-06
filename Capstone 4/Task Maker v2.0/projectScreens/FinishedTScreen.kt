package com.composetrails.projects.until6.projectScreens

import androidx.compose.foundation.layout.Arrangement
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
fun FinTS(tasks: MutableList<Task>, padding: PaddingValues) {
    val finishedTasks = tasks.filter { it.isDone }
 if(finishedTasks.isEmpty()){
     EmptyTaskSurface("No finished tasks yet!",padding)
 }
    else {
     LazyColumn(
         modifier = Modifier
             .fillMaxSize()
             .padding(padding),
         verticalArrangement = Arrangement.Top
     ) {
         items(finishedTasks.size) { index ->
             val task = finishedTasks[index]
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
                         onCheckedChange = null // ✅ disable toggling in finished view
                     )
                     Spacer(modifier = Modifier.width(10.dp))
                     Text(
                         text = task.title,
                         fontSize = 18.sp,
                         textDecoration = TextDecoration.LineThrough
                     )
                 }
             }
         }
     }
 }
}
