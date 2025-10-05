package com.literatrack.repo

import android.content.Context
import com.literatrack.db.BookDao
import com.literatrack.network.ConnectivityMonitor
import com.literatrack.network.GoogleBooksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideConnectivityMonitor(
        @ApplicationContext context: Context
    ): ConnectivityMonitor {
        return ConnectivityMonitor(context)
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        api: GoogleBooksApi,
        bookDao: BookDao,
        connectivityMonitor: ConnectivityMonitor
    ): BookRepository {
        return BookRepository(connectivityMonitor,api,bookDao)
    }
}

