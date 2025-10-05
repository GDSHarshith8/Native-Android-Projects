package com.literatrack.presentation.BookDetailScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literatrack.db.BookEntity
import com.literatrack.db.toBook
import com.literatrack.presentation.Book
import com.literatrack.presentation.BookStatus
import com.literatrack.repo.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(BookDetailUiState())
    val uiState: State<BookDetailUiState> = _uiState

    // New: SharedFlow for save success event to notify when book is saved successfully
    private val _saveSuccessEvent = MutableSharedFlow<BookStatus>()
    val saveSuccessEvent: SharedFlow<BookStatus> = _saveSuccessEvent.asSharedFlow()

    init {
        // Read the bookId passed as savedStateHandle argument
        val bookId: String = checkNotNull(savedStateHandle["bookId"]) {
            "bookId is required"
        }
        loadBook(bookId)
    }

    private fun loadBook(bookId: String) {
        viewModelScope.launch {
            // Show loading state
            _uiState.value = BookDetailUiState(isLoading = true)

            try {
                // ✅ Step 1: Try to load from local DB
                val localBookEntity = repository.getLocalBookById(bookId)

                if (localBookEntity != null) {
                    // ✅ Found in local DB — mark as saved
                    _uiState.value = BookDetailUiState(
                        isLoading = false,
                        book = localBookEntity.toBook(),
                        isSaved = true
                    )
                } else {
                    // ✅ Not found locally — fetch from remote
                    val remoteBookEntity = repository.getRemoteBookById(bookId)

                    _uiState.value = BookDetailUiState(
                        isLoading = false,
                        book = remoteBookEntity?.toBook(),
                        isSaved = false // ❗ Not saved locally
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Show error state if loading failed
                _uiState.value = BookDetailUiState(
                    isLoading = false,
                    book = null,
                    error = "Failed to load book",
                    isSaved = false // Assume not saved on failure
                )
            }
        }
    }

    fun insertBookWithStatus(status: BookStatus) {
        val currentBook = _uiState.value.book ?: return
        viewModelScope.launch {
            try {
                // Create a BookEntity with the selected status
                val bookEntity = BookEntity(
                    id = currentBook.id,
                    title = currentBook.title,
                    author = currentBook.author,
                    description = currentBook.description,
                    thumbnailUrl = currentBook.thumbnailUrl,
                    pageCount = currentBook.pageCount,
                    rating = currentBook.rating,
                    bookStatus = status
                )
                // Save the book in the repository
                repository.saveBook(bookEntity)

                // Emit the save success event with the selected status
                _saveSuccessEvent.emit(status)
            } catch (e: Exception) {
                e.printStackTrace()
                // Optionally update UI state with error info here
            }
        }
    }
}