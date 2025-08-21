package com.taskmaker.di

import android.content.Context
import androidx.room.Room
import com.taskmaker.db.TaskDao
import com.taskmaker.db.TaskDatabase
import com.taskmaker.repository.TaskRepository
import com.taskmaker.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "task_db"
        ).build()
    }

    @Provides
    fun provideDao(db: TaskDatabase): TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideRepository(dao: TaskDao): TaskRepository =
        TaskRepositoryImpl(dao)
}
