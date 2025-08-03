package com.taskmaker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaker.presentation.Task
import com.taskmaker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AllTasksVM @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    // Flow from Room, collected in Composables using collectAsStateWithLifecycle()
    val tasks = repo.getAllTasks()

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isDone = !task.isDone)
            repo.updateTask(updated)
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repo.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repo.deleteTask(task)
        }
    }
}

