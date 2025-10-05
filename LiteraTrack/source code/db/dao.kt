package com.literatrack.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.literatrack.presentation.BookStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(book: BookEntity)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("SELECT * FROM books WHERE bookStatus = :status")
    fun getBooksByStatusFlow(status: BookStatus): Flow<List<BookEntity>>

    @Query("UPDATE books SET bookStatus = :newStatus WHERE id = :bookId")
    suspend fun updateBookStatus(bookId: String, newStatus: BookStatus)

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): List<BookEntity>

    // NEW: Query to get all books from the database
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>
}