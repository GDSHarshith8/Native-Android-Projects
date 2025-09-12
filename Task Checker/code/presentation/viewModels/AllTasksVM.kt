package com.taskchecker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskchecker.db.Task
import com.taskchecker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTasksVM @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    // Expose all tasks as a StateFlow
    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleTaskDone(task: Task) {
        val updatedTask = task.copy(isDone = !task.isDone)
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }
}