package com.taskmaker.model

import android.content.Context
import androidx.room.Room
import com.taskmaker.db.TaskDao
import com.taskmaker.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModel {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
                context,
                TaskDatabase::class.java,
                "task_db"
            ).fallbackToDestructiveMigration(false) // Use this to auto-clear on schema mismatch
            .build()
    }

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()
}
