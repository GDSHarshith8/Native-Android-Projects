package com.literatrack.network

import com.literatrack.db.BookEntity
import com.literatrack.presentation.Book
import com.literatrack.presentation.BookStatus

// Ensures all thumbnail URLs use HTTPS.
fun normalizeThumbnailUrl(rawUrl: String?): String? {
    if (rawUrl.isNullOrBlank()) return null
    return rawUrl.replace("http://", "https://")
}

/**
 * Maps a NetworkVolumeItem (from the API) to a Book (for UI display).
 * This is used to display search results on the screen before they are saved to the database.
 */
fun NetworkVolumeItem.toBook(): Book {
    val thumbnailUrl = this.volumeInfo?.imageLinks?.large ?: this.volumeInfo?.imageLinks?.thumbnail

    val safeAuthors = when (val authorField = volumeInfo?.authors) {
        is List<*> -> authorField.filterIsInstance<String>().joinToString(", ")
        is String -> authorField
        else -> "Unknown Author"
    }

    return Book(
        id = this.id,
        title = this.volumeInfo?.title ?: "na",
        author = safeAuthors,
        description = this.volumeInfo?.description ?: "",
        thumbnailUrl = normalizeThumbnailUrl(thumbnailUrl),
        pageCount = this.volumeInfo?.pageCount ?: 0,
        rating = this.volumeInfo?.averageRating,
        bookStatus = BookStatus.ToBeRead
    )
}

/**
 * Maps a NetworkVolumeItem (from the API) to a BookEntity (for Room database).
 * This is used to persist books into the local database.
 */
fun NetworkVolumeItem.toBookEntity(): BookEntity {
    val thumbnailUrl = this.volumeInfo?.imageLinks?.large ?: this.volumeInfo?.imageLinks?.thumbnail

    val safeAuthors = when (val authorField = volumeInfo?.authors) {
        is List<*> -> authorField.filterIsInstance<String>().joinToString(", ")
        is String -> authorField
        else -> "Unknown Author"
    }

    return BookEntity(
        id = this.id,
        title = this.volumeInfo?.title ?: "na",
        author = safeAuthors,
        description = this.volumeInfo?.description ?: "",
        thumbnailUrl = normalizeThumbnailUrl(thumbnailUrl),
        pageCount = this.volumeInfo?.pageCount ?: 0,
        rating = volumeInfo?.averageRating,
        bookStatus = BookStatus.ToBeRead
    )
}