package com.literatrack.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.literatrack.presentation.BookStatus

@Entity(
    tableName = "books",
    indices = [Index(value = ["bookStatus"])] // For faster queries filtering by status
)
@TypeConverters(BookStatusConverter::class)
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String?,
    val thumbnailUrl: String?,
    val pageCount: Int?,
    val rating: Double?,
    val bookStatus: BookStatus
)

// TypeConverter to store enum as string in Room DB
class BookStatusConverter {

    @TypeConverter
    fun fromBookStatus(status: BookStatus): String = status.name

    @TypeConverter
    fun toBookStatus(value: String): BookStatus = enumValueOf(value)
}
