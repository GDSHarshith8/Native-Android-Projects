package com.musicplayer.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.musicplayer.datastore.UserPreferencesViewModel
import com.musicplayer.network.Track
import com.musicplayer.presentation.viewModels.PlayerViewModel
import com.musicplayer.presentation.viewModels.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen2M(
    playerViewModel: PlayerViewModel,
    viewModel: SearchViewModel = hiltViewModel(),
    onThemeSelected: (AppTheme) -> Unit,
    onNavigateBack: () -> Unit = {},
    onTrackSelected: (Track) -> Unit = {}
) {
    val userPreferencesViewModel: UserPreferencesViewModel = hiltViewModel()
    val tracks by viewModel.filteredTracks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HMcontent(
                userPreferencesViewModel = userPreferencesViewModel,
                onThemeSelected = onThemeSelected,
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Search Something") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = { Text("Start typing...") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                // Sorting Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val sortModes = listOf(
                        SearchViewModel.SortMode.AZ to "Name",
                        SearchViewModel.SortMode.DURATION to "Duration",
                        SearchViewModel.SortMode.CLOSEST_MATCH to "Closest Match"
                    )
                    val currentSortMode by viewModel.sortMode.collectAsState()

                    sortModes.forEachIndexed { index, (mode, label) ->
                        SortChip(
                            text = label,
                            selected = currentSortMode == mode,
                            onClick = { viewModel.onSortModeChanged(mode) }
                        )
                        if (index < sortModes.lastIndex) Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    tracks.isEmpty() && error != null -> {
                        // Show no internet / network error message
                        EmptyMsg(message = error!!)
                    }

                    tracks.isEmpty() && searchQuery.isNotBlank() -> {
                        // No results for query
                        EmptyMsg(message = "No results found for \"$searchQuery\"")
                    }

                    tracks.isEmpty() && searchQuery.isBlank() -> {
                        // Initial empty state
                        EmptyMsg(message = "Search for songs, albums, soundtracks, artists...")
                    }

                    else -> {
                        // Show track list (cached or fetched)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            itemsIndexed(tracks) { index, track ->
                                TrackItem(track = track) {
                                    playerViewModel.playTrack(context, tracks, index)
                                    onTrackSelected(track)
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    playerViewModel: PlayerViewModel,
    viewModel: SearchViewModel = hiltViewModel(),
    onThemeSelected: (AppTheme) -> Unit,
    onNavigateBack: () -> Unit = {},
    onTrackSelected: (Track) -> Unit = {},
    onMenuClick: () -> Unit = {} // drawer toggle from NavGraph
) {
    val userPreferencesViewModel: UserPreferencesViewModel = hiltViewModel()
    val tracks by viewModel.filteredTracks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Something") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { // open single root drawer
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = { Text("Start typing...") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            // Sorting Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val sortModes = listOf(
                    SearchViewModel.SortMode.AZ to "Name",
                    SearchViewModel.SortMode.DURATION to "Duration",
                    SearchViewModel.SortMode.CLOSEST_MATCH to "Closest Match"
                )
                val currentSortMode by viewModel.sortMode.collectAsState()

                sortModes.forEachIndexed { index, (mode, label) ->
                    SortChip(
                        text = label,
                        selected = currentSortMode == mode,
                        onClick = { viewModel.onSortModeChanged(mode) }
                    )
                    if (index < sortModes.lastIndex) Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                tracks.isEmpty() && error != null -> {
                    EmptyMsg(message = error!!)
                }

                tracks.isEmpty() && searchQuery.isNotBlank() -> {
                    EmptyMsg(message = "No results found for \"$searchQuery\"")
                }

                tracks.isEmpty() && searchQuery.isBlank() -> {
                    EmptyMsg(message = "Search for songs, albums, soundtracks, artists...")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        itemsIndexed(tracks) { index, track ->
                            TrackItem(track = track) {
                                playerViewModel.playTrack(context, tracks, index)
                                onTrackSelected(track)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!track.imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = track.imageUrl,
                contentDescription = track.title,
                modifier = Modifier.size(48.dp).padding(end = 12.dp)
            )
        }
        Column {
            Text(track.title, style = MaterialTheme.typography.bodyLarge)
            Text(track.artist, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        )
    )
}