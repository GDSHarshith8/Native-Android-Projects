package com.taskmaker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaker.presentation.Task
import com.taskmaker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnfinishedTasksVM @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    private val _unfinishedTasks = MutableStateFlow<List<Task>>(emptyList())
    val unfinishedTasks: StateFlow<List<Task>> = _unfinishedTasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repo.getAllTasks()
                .map { list -> list.filter { !it.isDone } }
                .collect { _unfinishedTasks.value = it }
        }
    }

    fun updateTaskStatus(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = isChecked)
            repo.updateTask(updatedTask)
            loadTasks() // Refresh list after update
        }
    }

}
