package com.literatrack.presentation.LibScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literatrack.db.BookEntity
import com.literatrack.db.toBook
import com.literatrack.db.toBookEntity
import com.literatrack.presentation.Book
import com.literatrack.presentation.BookStatus
import com.literatrack.repo.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(BookStatus.Reading)
    val selectedStatus: StateFlow<BookStatus> = _selectedStatus

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _selectedStatus
                .flatMapLatest { status ->
                    _isLoading.value = true // start loading before collecting books
                    repository.getBooksByStatusFlow(status)
                }
                .map { entities -> entities.map { it.toBook() } }
                .onEach { _isLoading.value = false } // stop loading once data arrives
                .collect { bookList ->
                    _books.value = bookList
                }
        }
    }


    fun onStatusSelected(status: BookStatus) {
        _selectedStatus.value = status
    }

    fun updateBookStatus(book: Book, newStatus: BookStatus) {
        viewModelScope.launch {
            repository.updateBookStatus(book.id, newStatus)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book.toBookEntity())
        }
    }
}