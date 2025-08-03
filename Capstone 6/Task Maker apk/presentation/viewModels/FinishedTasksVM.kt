package com.taskmaker.presentation.viewModels

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaker.presentation.Task
import com.taskmaker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinishedTasksVM @Inject constructor(
    private val repo: TaskRepository
) : ViewModel() {

    // Expose filtered Flow directly
    val finishedTasks: Flow<List<Task>> = repo.getAllTasks()
        .map { list -> list.filter { it.isDone } }
}
