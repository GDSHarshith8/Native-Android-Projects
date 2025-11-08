package com.musicplayer.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.network.Track
import com.musicplayer.network.TrackRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val remoteDataSource: TrackRemoteDataSource
) : ViewModel() {

    private val _allTracks = MutableStateFlow<List<Track>>(emptyList())
    val allTracks: StateFlow<List<Track>> = _allTracks

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    enum class SortMode { AZ, DURATION, CLOSEST_MATCH }

    private val _sortMode = MutableStateFlow(SortMode.CLOSEST_MATCH)
    val sortMode: StateFlow<SortMode> = _sortMode

    fun onSortModeChanged(mode: SortMode) {
        _sortMode.value = mode
    }

    init {
        fetchTracks()
    }

    val filteredTracks: StateFlow<List<Track>> = combine(
        _allTracks,
        _searchQuery.debounce(300),
        _sortMode
    ) { tracks, query, sortMode ->
        if (tracks.isEmpty()) return@combine emptyList() // no tracks at all

        val filtered = if (query.isBlank()) tracks else {
            tracks.filter { it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) }
        }

        when (sortMode) {
            SortMode.AZ -> filtered.sortedBy { it.title.lowercase() }
            SortMode.DURATION -> filtered.sortedBy { it.durationInSeconds }
            SortMode.CLOSEST_MATCH -> filtered.sortedBy {
                val lowerQuery = query.lowercase()
                val titleScore = it.title.lowercase().indexOf(lowerQuery).takeIf { it >= 0 } ?: Int.MAX_VALUE
                val artistScore = it.artist.lowercase().indexOf(lowerQuery).takeIf { it >= 0 } ?: Int.MAX_VALUE
                minOf(titleScore, artistScore)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun fetchTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val tracks = remoteDataSource.getTracks()
                _allTracks.value = tracks
            } catch (e: Exception) {
                _error.value = when {
                    e.localizedMessage?.contains("Unable to resolve host", true) == true ||
                            e is java.net.UnknownHostException -> "No internet connection."
                    else -> "Failed to load tracks: ${e.localizedMessage ?: "Unknown error"}"
                }
                // **do not clear _allTracks** — keep any cached tracks
            } finally {
                _isLoading.value = false
            }
        }
    }
}