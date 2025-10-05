package com.literatrack.presentation

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String?,
    val thumbnailUrl: String?,
    val pageCount: Int?,
    val rating: Double?,
    val bookStatus: BookStatus
)

enum class BookStatus(val label: String) {
    ToBeRead("Wishlist"),
    Reading("Reading"),
    Completed("Completed"),
}