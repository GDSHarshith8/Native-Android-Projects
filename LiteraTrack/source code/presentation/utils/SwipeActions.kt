package com.literatrack.presentation.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.literatrack.presentation.Book
import com.literatrack.presentation.BookStatus

@Composable
fun SwipeableBookItem(
    book: Book,
    currentStatus: BookStatus,
    onStatusChange: (Book, BookStatus) -> Unit,
    onDelete: (Book) -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Previously end to start logic here
                    when (currentStatus) {
                        BookStatus.Reading -> {
                            onStatusChange(book, BookStatus.Completed)
                            true
                        }
                        BookStatus.ToBeRead -> {
                            onStatusChange(book, BookStatus.Reading)
                            true
                        }
                        BookStatus.Completed -> false // Resist swipe right
                    }
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // Previously start to end logic here
                    when (currentStatus) {
                        BookStatus.Reading -> {
                            onStatusChange(book, BookStatus.ToBeRead)
                            true
                        }
                        BookStatus.Completed -> {
                            onStatusChange(book, BookStatus.Reading)
                            true
                        }
                        BookStatus.ToBeRead -> false // Resist swipe left
                    }
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            BookSwipeBackground(
                dismissState = dismissState,
                currentStatus = currentStatus
            )
        },
    ) {
        BookCard(
            book = book,
            onClick = onClick,
            onDelete = onDelete,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BookSwipeBackground(
    dismissState: SwipeToDismissBoxState,
    currentStatus: BookStatus
) {
    val direction = dismissState.dismissDirection

    val swipeInfo: Pair<ImageVector?, Color> = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> {
            when (currentStatus) {
                BookStatus.Reading -> Icons.Filled.CheckCircle to Color(0xFF1E88E5)
                BookStatus.ToBeRead -> Icons.Filled.PlayArrow to Color(0xFF4CAF50)
                BookStatus.Completed -> null to Color.Transparent
            }
        }
        SwipeToDismissBoxValue.EndToStart -> {
            when (currentStatus) {
                BookStatus.Reading -> Icons.Filled.BookmarkBorder to Color(0xFF4CAF50)
                BookStatus.Completed -> Icons.Filled.PlayArrow to Color(0xFFFE6B3C)
                BookStatus.ToBeRead -> null to Color.Transparent
            }
        }
        else -> null to Color.Transparent
    }

    val (icon, color) = swipeInfo

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
        }
    }
}