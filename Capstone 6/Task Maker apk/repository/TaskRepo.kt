package com.taskmaker.repository

import com.taskmaker.presentation.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
}
