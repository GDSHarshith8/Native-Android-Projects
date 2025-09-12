package com.taskchecker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskchecker.db.Task
import com.taskchecker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTasksVM @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }
}
