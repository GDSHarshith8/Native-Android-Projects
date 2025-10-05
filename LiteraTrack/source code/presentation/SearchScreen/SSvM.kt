package com.literatrack.presentation.SearchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literatrack.db.toBook
import com.literatrack.presentation.Book
import com.literatrack.repo.BookRepository
import com.literatrack.network.ConnectivityMonitor
import com.literatrack.network.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults: StateFlow<List<Book>> = _searchResults

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _searchError = MutableStateFlow("")
    val searchError: StateFlow<String> = _searchError

    private val _savedBooks = MutableStateFlow<List<Book>>(emptyList())
    val savedBooks: StateFlow<List<Book>> = _savedBooks

    init {
        // ✅ Observe internet connectivity
        viewModelScope.launch {
            connectivityMonitor.isConnected
                .distinctUntilChanged()
                .collect { connected ->
                    _isConnected.value = connected
                }
        }

        // ✅ Live database observation with cancellation-safe block
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                bookRepository.getAllBooks()
                    .map { entities -> entities.map { it.toBook() } }
                    .distinctUntilChanged()
                    .collect { books ->
                        _savedBooks.value = books
                    }
            }.onFailure { e ->
                if (e !is CancellationException) {
                    _savedBooks.value = emptyList()
                }
            }
        }

        // ✅ Search functionality
        viewModelScope.launch {
            combine(_searchQuery.debounce(300), _isConnected) { query, connected ->
                query to connected
            }
                .filter { (query, _) -> query.isNotBlank() }
                .mapLatest { (query, connected) ->
                    _isLoading.value = true
                    _searchError.value = ""

                    val results = try {
                        if (connected) {
                            bookRepository.searchGoogleBooks(query)
                        } else {
                            val localResults = bookRepository.searchLocalBooks(query).map { it.toBook() }

                            if (localResults.isEmpty()) {
                                _searchError.value = "Not found among saved books!"
                            }

                            localResults
                        }
                    } catch (e: CancellationException) {
                        throw e // Let coroutine handle expected cancellation
                    } catch (e: NoInternetException) {
                        _searchError.value = "You're offline. Showing saved books only."

                        // Try local fallback
                        val fallback = bookRepository.searchLocalBooks(query).map { it.toBook() }
                        if (fallback.isEmpty()) {
                            _searchError.value = "You're offline and this book isn't saved."
                        }
                        fallback
                    } catch (e: Exception) {
                        val fallback = bookRepository.searchLocalBooks(query).map { it.toBook() }

                        if (fallback.isEmpty()) {
                            _searchError.value = "Something went wrong, and no saved books were found."
                        } else {
                            _searchError.value = "Showing saved books due to a fetch error."
                        }

                        fallback
                    }

                    if (results.isEmpty() && _searchError.value.isEmpty()) {
                        _searchError.value = "No results found."
                    }

                    results
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    _isLoading.value = false
                    _searchResults.value = it
                }
                .launchIn(this)
        }
    }

    /**
     * Updates the search query and triggers search.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        // ✅ Reset error & results on clear
        if (query.isEmpty()) {
            _searchError.value = ""
            _searchResults.value = emptyList()
        }
    }
}
