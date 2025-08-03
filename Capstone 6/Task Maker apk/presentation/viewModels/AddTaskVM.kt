package com.taskmaker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaker.presentation.Task
import com.taskmaker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class `AddTaskVM.kt` @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    fun addTask(task: Task) {
        viewModelScope.launch {
            repo.insertTask(task)
        }
    }
}
