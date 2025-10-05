package com.literatrack.db

import com.literatrack.presentation.Book

/**
 * Maps a BookEntity (from the database) to a Book (for the UI).
 * This is used for displaying books from the local library.
 */
fun BookEntity.toBook(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description,
        thumbnailUrl = this.thumbnailUrl,
        pageCount = this.pageCount,
        rating = this.rating,
        bookStatus = this.bookStatus
    )
}

/**
 * Maps a Book (from the UI) to a BookEntity (for the database).
 * This is crucial for saving a book from the search screen to the local DB,
 * as the ViewModel works with the UI model.
 */
fun Book.toBookEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description,
        thumbnailUrl = this.thumbnailUrl,
        pageCount = this.pageCount,
        rating = this.rating,
        bookStatus = this.bookStatus
    )
}