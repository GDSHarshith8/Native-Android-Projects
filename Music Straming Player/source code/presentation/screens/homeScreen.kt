package com.musicplayer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicplayer.datastore.UserPreferencesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2M(
    userPreferencesViewModel: UserPreferencesViewModel,
    onThemeSelected: (AppTheme) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedTheme by remember { mutableStateOf(AppTheme.SYSTEM_DEFAULT) }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HMcontent(
                userPreferencesViewModel = userPreferencesViewModel,
                onThemeSelected = { theme ->
                    onThemeSelected(theme) // triggers MainActivity -> ViewModel -> recomposition
                },
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Music Streaming Player",
                            style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 0.5.sp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onNavigateToSearch()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                EmptyMsg(
                    message = "Search for \nalbums, \nsongs, \nsoundtracks, \nartists, \npodcasts \n🎧"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userPreferencesViewModel: UserPreferencesViewModel,
    onThemeSelected: (AppTheme) -> Unit,
    onNavigateToSearch: () -> Unit,
    onMenuClick: () -> Unit // pass drawer toggle from NavGraph
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Music Streaming Player",
                        style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 0.5.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { // open drawer
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmptyMsg(
                message = "Search for \nalbums, \nsongs, \nsoundtracks, \nartists, \npodcasts \n🎧"
            )
        }
    }
}

@Composable
fun EmptyMsg(
    message: String,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = Modifier
            .padding(padding)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .defaultMinSize(minWidth = 220.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}