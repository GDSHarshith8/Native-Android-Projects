package com.taskmaker.repository

import com.taskmaker.db.TaskDao
import com.taskmaker.presentation.Task
import jakarta.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
): TaskRepository {
    override fun getAllTasks() = dao.getAllTasks()
    override suspend fun insertTask(task: Task) = dao.insertTask(task)
    override suspend fun deleteTask(task: Task) = dao.deleteTask(task)
    override suspend fun updateTask(task: Task) = dao.updateTask(task)
}
