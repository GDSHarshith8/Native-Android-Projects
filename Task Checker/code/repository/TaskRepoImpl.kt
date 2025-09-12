package com.taskchecker.repository

import com.taskchecker.db.Task
import com.taskchecker.db.TaskDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
): TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> = dao.getAllTasks()
    override suspend fun insertTask(task: Task) = dao.insertTask(task)
    override suspend fun deleteTask(task: Task) = dao.deleteTask(task)
    override suspend fun updateTask(task: Task) = dao.updateTask(task)
}
