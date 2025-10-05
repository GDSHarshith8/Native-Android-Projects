package com.literatrack.repo

import com.literatrack.db.BookDao
import com.literatrack.db.BookEntity
import com.literatrack.network.ConnectivityMonitor
import com.literatrack.network.GoogleBooksApi
import com.literatrack.network.NoInternetException
import com.literatrack.network.NetworkVolumeItem
import com.literatrack.network.toBook
import com.literatrack.network.toBookEntity
import com.literatrack.presentation.Book
import com.literatrack.presentation.BookStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
    private val api: GoogleBooksApi,
    private val bookDao: BookDao
) {
    /**
     * Searches for books from the Google Books API and maps the results to Book (UI model).
     * Newly searched books are always set to 'ToBeRead' status.
     */
    suspend fun searchGoogleBooks(query: String): List<Book> {
        val isOnline = connectivityMonitor.isConnected.first()

        if (!isOnline)
            throw NoInternetException()

        val response = api.searchBooks(query)
        val books = response.items?.mapNotNull { item ->
            try {
                item.toBook()
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()

        return books
    }

    /**
     * Fetches a single book by ID
     */
    // Fetch only from local DB
    suspend fun getLocalBookById(bookId: String): BookEntity? {
        return bookDao.getBookById(bookId)
    }

    // Fetch only from remote API
    suspend fun getRemoteBookById(bookId: String): BookEntity? {
        val isOnline = connectivityMonitor.isConnected.first()
        if (!isOnline) throw NoInternetException()

        return try {
            val response: NetworkVolumeItem = api.getBookById(bookId)
            response.toBookEntity()
        } catch (e: Exception) {
            null
        }
    }



    /**
     * Saves or updates a book in the local Room database.
     */
    suspend fun saveBook(bookEntity: BookEntity) {
        bookDao.insertOrUpdate(bookEntity)
    }

    /**
     * Deletes the saved book from repo
     */
    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBookById(book.id)
    }

    /**
     * Gets all books from the database that match a specific status - observe live updates.
     */
    fun getBooksByStatusFlow(status: BookStatus): Flow<List<BookEntity>> {
        return bookDao.getBooksByStatusFlow(status)
    }

    suspend fun updateBookStatus(bookId: String, newStatus: BookStatus) {
        bookDao.updateBookStatus(bookId, newStatus)
    }

    // Search local books by query
    fun searchLocalBooks(query: String): List<BookEntity> {
        return bookDao.searchBooks(query)
    }

    // Get all books as live flow
    fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }
}