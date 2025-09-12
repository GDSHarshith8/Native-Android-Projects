package com.taskchecker.repository

import com.taskchecker.db.Task
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

//@Singleton
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
}
