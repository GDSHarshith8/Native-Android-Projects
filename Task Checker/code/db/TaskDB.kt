package com.taskchecker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskchecker.db.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
