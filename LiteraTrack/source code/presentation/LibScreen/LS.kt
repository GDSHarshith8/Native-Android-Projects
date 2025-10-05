package com.literatrack.presentation.LibScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.literatrack.presentation.BookStatus
import com.literatrack.presentation.navigation.AppRoutes
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.literatrack.presentation.utils.AppTheme
import com.literatrack.presentation.utils.EmptyBooksSurface
import com.literatrack.presentation.utils.HMcontent
import com.literatrack.presentation.utils.SwipeableBookItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    filterStatus: String = BookStatus.Reading.name, // default to Reading
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    // On first composition, update the ViewModel’s selectedStatus based on this param:
    LaunchedEffect(filterStatus) {
        viewModel.onStatusSelected(BookStatus.valueOf(filterStatus))
    }

    val isLoading by viewModel.isLoading.collectAsState()

    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val books by viewModel.books.collectAsState()

    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HMcontent(
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeChange,
                drawerState = drawerState,
                onShowOnboarding = {
                    scope.launch {
                        navController.navigate(AppRoutes.Onboarding.route)
                    }
                }
            )
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("Library") },
                    actions = {
                        IconButton(onClick = { navController.navigate(AppRoutes.Search.route) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = { // Use the bottomBar parameter
                BookStatusChipRow(
                    selected = selectedStatus,
                    onStatusSelected = viewModel::onStatusSelected
                )
//            BookStatusBottomNav(
//                selectedStatus = selectedStatus,
//                onStatusSelected = viewModel::onStatusSelected
//            )
            }
        ) { padding ->
            // Apply the padding provided by the Scaffold to the content
            // This ensures the content is correctly positioned below the TopAppBar and above the BottomBar

            val emptyMessage = when (selectedStatus) {
                BookStatus.ToBeRead -> "No books to read yet!\n\nSearch for a book to add!"
                BookStatus.Reading -> "No books currently being read!\n\nSearch for a book to add!"
                BookStatus.Completed -> "No books completed yet!\n\nKeep reading to fill this section!"
            }

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator()

                    books.isEmpty() -> EmptyBooksSurface(emptyMessage)

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(books, key = { it.id }) { book ->
                                SwipeableBookItem(
                                    book = book,
                                    currentStatus = selectedStatus,
                                    onStatusChange = viewModel::updateBookStatus,
                                    onDelete = viewModel::deleteBook,
                                    onClick = {
                                        navController.navigate(
                                            AppRoutes.Detail.createRoute(
                                                book.id
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookStatusChipRow(
    selected: BookStatus,
    onStatusSelected: (BookStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 380.dp)
            .padding(10.dp)
            .navigationBarsPadding()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(34.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookStatus.entries.forEach { status ->
            val isSelected = selected == status
            val icon = when (status) {
                BookStatus.ToBeRead -> Icons.Default.BookmarkBorder
                BookStatus.Reading -> Icons.AutoMirrored.Filled.MenuBook
                BookStatus.Completed -> Icons.Filled.CheckCircle
            }

            FilterChip(
                elevation = if (isSelected) {
                    FilterChipDefaults.elevatedFilterChipElevation()
                } else {
                    FilterChipDefaults.filterChipElevation()
                },
                border = if (isSelected) null else BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                selected = isSelected,
                onClick = { onStatusSelected(status) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = status.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status.label,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    },
                    labelColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                modifier = Modifier
                    .height(44.dp)
                    .padding(2.dp),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun BookStatusBottomNav(
    selectedStatus: BookStatus,
    onStatusSelected: (BookStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.navigationBarsPadding()
    ) {
        BookStatus.entries.forEach { status ->
            val icon = when (status) {
                BookStatus.ToBeRead -> Icons.Filled.BookmarkBorder
                BookStatus.Reading -> Icons.AutoMirrored.Filled.MenuBook
                BookStatus.Completed -> Icons.Filled.CheckCircle
            }

            NavigationBarItem(
                icon = { Icon(icon, contentDescription = status.label) },
                label = { Text(status.label) },
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                alwaysShowLabel = true
            )
        }
    }
}