package com.taskchecker.di

import android.content.Context
import androidx.room.Room
import com.taskchecker.db.TaskDao
import com.taskchecker.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


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

    @Singleton
    @Provides
    fun provideDao(db: TaskDatabase): TaskDao = db.taskDao()
}
