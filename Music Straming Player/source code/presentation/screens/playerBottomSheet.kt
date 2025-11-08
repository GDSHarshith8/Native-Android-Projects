package com.musicplayer.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.musicplayer.network.Track
import com.musicplayer.presentation.viewModels.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomSheetHalfPlayer(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    mainContent: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    val scope = rememberCoroutineScope()
    val maxHeightDp = 400.dp
    val minHeightDp = 70.dp
    val density = LocalDensity.current
    val maxHeightPx = with(density) { maxHeightDp.toPx() }
    val minHeightPx = with(density) { minHeightDp.toPx() }

    // Main content padding for mini-player
    mainContent(PaddingValues(bottom = if (currentTrack != null) minHeightDp else 0.dp))

    val targetHeight = remember { Animatable(minHeightPx) }
    var expanded by rememberSaveable { mutableStateOf(false) } // Remember across config changes

    val playPauseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    var sliderPosition by rememberSaveable { mutableStateOf(progress.toFloat()) } // Smooth dragging

    currentTrack?.let { track ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { targetHeight.value.toDp() })
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                val midPointPx = (minHeightPx + maxHeightPx) / 2
                                expanded = targetHeight.value > midPointPx
                                scope.launch {
                                    targetHeight.animateTo(
                                        targetValue = if (expanded) maxHeightPx else minHeightPx,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    targetHeight.snapTo(
                                        (targetHeight.value - dragAmount).coerceIn(minHeightPx, maxHeightPx)
                                    )
                                }
                            }
                        )
                    }
            ) {
                if (expanded) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .padding(vertical = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    // Expanded content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = track.imageUrl,
                            contentDescription = track.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(track.title, style = MaterialTheme.typography.titleLarge)
                        Text(track.artist, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress / seek bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                formatTime((sliderPosition / 1000).toInt()),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Slider(
                                value = sliderPosition,
                                onValueChange = { sliderPosition = it }, // Smooth dragging
                                onValueChangeFinished = {
                                    playerViewModel.seekTo(sliderPosition.toLong())
                                },
                                valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                                modifier = Modifier.weight(1f),
                                thumb = { SliderDefaults.Thumb(
                                    enabled = true,
                                    interactionSource = remember { MutableInteractionSource() },
                                    modifier = Modifier.size(16.dp)
                                ) },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                                )
                            )

                            Text(
                                formatTime((duration / 1000).toInt()),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                FilledTonalIconButton(
                                    onClick = { playerViewModel.playPrevious() },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                                }
                            }

                            FilledTonalIconButton(
                                onClick = { playerViewModel.togglePlayPause() },
                                modifier = Modifier
                                    .size(64.dp)
                                    .scale(playPauseScale)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                FilledTonalIconButton(
                                    onClick = { playerViewModel.playNext() },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                                }
                            }
                        }
                    }

                } else {
                    // Mini-player
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(minHeightDp)
                            .clickable {
                                expanded = true
                                scope.launch {
                                    targetHeight.animateTo(
                                        maxHeightPx,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                }
                            }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = track.imageUrl,
                            contentDescription = track.title,
                            modifier = Modifier.size(48.dp).padding(end = 8.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.bodyLarge)
                            Text(track.artist, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(
                            onClick = { playerViewModel.togglePlayPause() },
                            modifier = Modifier.scale(playPauseScale)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomSheetFull(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    mainContent: @Composable (PaddingValues) -> Unit
) {
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val minHeightDp = 70.dp
    val maxHeightDp = screenHeightDp
    val density = LocalDensity.current
    val minHeightPx = with(density) { minHeightDp.toPx() }
    val maxHeightPx = with(density) { maxHeightDp.toPx() }

    mainContent(PaddingValues(bottom = if (currentTrack != null) minHeightDp else 0.dp))

    val targetHeight = remember { Animatable(minHeightPx) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    var isUserDragging by remember { mutableStateOf(false) }
    var sliderPosition by rememberSaveable { mutableStateOf(progress.toFloat()) }

    // Sync slider with player progress
    LaunchedEffect(progress) {
        if (!isUserDragging) {
            sliderPosition = progress.toFloat()
        }
    }

    currentTrack?.let { track ->
        val expansionFraction = (targetHeight.value - minHeightPx) / (maxHeightPx - minHeightPx)
        val artworkHeightDp = minHeightDp + (300.dp - minHeightDp) * expansionFraction
        val fadeInAlpha = expansionFraction.coerceIn(0f, 1f)
        val playPauseScale by animateFloatAsState(
            targetValue = if (isPlaying) 1.2f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { targetHeight.value.toDp() })
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    val midPointPx = (minHeightPx + maxHeightPx) / 2
                                    expanded = targetHeight.value > midPointPx
                                    scope.launch {
                                        targetHeight.animateTo(
                                            targetValue = if (expanded) maxHeightPx else minHeightPx,
                                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                        )
                                    }
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    scope.launch {
                                        targetHeight.snapTo(
                                            (targetHeight.value - dragAmount).coerceIn(minHeightPx, maxHeightPx)
                                        )
                                    }
                                }
                            )
                        }
                ) {
                    // Mini-player
                    if (!expanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(minHeightDp)
                                .clickable {
                                    expanded = true
                                    scope.launch {
                                        targetHeight.animateTo(
                                            maxHeightPx,
                                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                        )
                                    }
                                }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = track.imageUrl,
                                contentDescription = track.title,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 8.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, style = MaterialTheme.typography.bodyLarge)
                                Text(track.artist, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(
                                onClick = { playerViewModel.togglePlayPause() },
                                modifier = Modifier.scale(playPauseScale)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play"
                                )
                            }
                        }
                    }

                    // Expanded content
                    if (expansionFraction > 0.05f) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .graphicsLayer { alpha = fadeInAlpha }
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(artworkHeightDp)
                            ) {
                                AsyncImage(
                                    model = track.imageUrl,
                                    contentDescription = track.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(track.title, style = MaterialTheme.typography.titleLarge)
                            Text(track.artist, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Slider with auto-updating
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    formatTime((sliderPosition / 1000).toInt()),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Slider(
                                    value = sliderPosition,
                                    onValueChange = {
                                        sliderPosition = it
                                        isUserDragging = true
                                    },
                                    onValueChangeFinished = {
                                        playerViewModel.seekTo(sliderPosition.toLong())
                                        isUserDragging = false
                                    },
                                    valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                                    modifier = Modifier.weight(1f),
                                    thumb = { SliderDefaults.Thumb(interactionSource = remember { MutableInteractionSource() }) },
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                                    )
                                )
                                Text(
                                    formatTime((duration / 1000).toInt()),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    FilledTonalIconButton(
                                        onClick = { playerViewModel.playPrevious() },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                                    }
                                }
                                FilledTonalIconButton(
                                    onClick = { playerViewModel.togglePlayPause() },
                                    modifier = Modifier.size(64.dp).scale(playPauseScale)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    FilledTonalIconButton(
                                        onClick = { playerViewModel.playNext() },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Default.SkipNext, contentDescription = "Next")
                                    }
                                }
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
fun MusicBottomSheet(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    mainContent: @Composable (PaddingValues) -> Unit
) {
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val minHeightDp = 70.dp
    val maxHeightDp = screenHeightDp
    val density = LocalDensity.current
    val minHeightPx = with(density) { minHeightDp.toPx() }
    val maxHeightPx = with(density) { maxHeightDp.toPx() }

    mainContent(PaddingValues(bottom = if (currentTrack != null) minHeightDp else 0.dp))

    val targetHeight = remember { Animatable(minHeightPx) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    var isUserDragging by remember { mutableStateOf(false) }
    var sliderPosition by rememberSaveable { mutableStateOf(progress.toFloat()) }

    // Sync slider with player progress
    LaunchedEffect(progress) {
        if (!isUserDragging) {
            sliderPosition = progress.toFloat()
        }
    }

    currentTrack?.let { track ->
        MusicBottomSheetContainer(
            track = track,
            isPlaying = isPlaying,
            sliderPosition = sliderPosition,
            onSliderChange = { sliderPosition = it },
            onSliderChangeFinished = {
                playerViewModel.seekTo(sliderPosition.toLong())
                isUserDragging = false
            },
            duration = duration,
            targetHeight = targetHeight,
            minHeightPx = minHeightPx,
            maxHeightPx = maxHeightPx,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            isUserDragging = isUserDragging,
            onUserDraggingChange = { isUserDragging = it },
            density = density,
            scope = scope,
            togglePlayPause = { playerViewModel.togglePlayPause() },
            playPrevious = { playerViewModel.playPrevious() },
            playNext = { playerViewModel.playNext() }
        )
    }
}

@Composable
fun MusicBottomSheetContainer(
    track: Track,
    isPlaying: Boolean,
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit,
    duration: Long,
    targetHeight: Animatable<Float, AnimationVector1D>,
    minHeightPx: Float,
    maxHeightPx: Float,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isUserDragging: Boolean,
    onUserDraggingChange: (Boolean) -> Unit,
    density: Density,
    scope: CoroutineScope,
    togglePlayPause: () -> Unit,
    playPrevious: () -> Unit,
    playNext: () -> Unit
) {
    val minArtworkSizeDp = 70.dp // For clarity, though it's the same as minHeightDp
    val maxArtworkSizeDp = 400.dp // ⭐ Increased target size for artwork

    // Direct calculation of derived values
    val expansionFraction = ((targetHeight.value - minHeightPx) / (maxHeightPx - minHeightPx)).coerceIn(0f, 1f)

    // Update this line to use the larger maxArtworkSizeDp
    val artworkHeightDp = minArtworkSizeDp + (maxArtworkSizeDp - minArtworkSizeDp) * expansionFraction

    val fadeInAlpha = expansionFraction

    val playPauseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { targetHeight.value.toDp() })
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                val midPointPx = (minHeightPx + maxHeightPx) / 2
                                val willExpand = targetHeight.value > midPointPx
                                onExpandedChange(willExpand)
                                scope.launch {
                                    targetHeight.animateTo(
                                        targetValue = if (willExpand) maxHeightPx else minHeightPx,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    targetHeight.snapTo(
                                        (targetHeight.value - dragAmount).coerceIn(minHeightPx, maxHeightPx)
                                    )
                                }
                            }
                        )
                    }
            ) {
                // Mini Player
                MiniPlayer(
                    track = track,
                    isPlaying = isPlaying,
                    minHeightDp = with(density) { minHeightPx.toDp() },
                    expanded = expanded,
                    onExpand = {
                        onExpandedChange(true)
                        scope.launch {
                            targetHeight.animateTo(
                                maxHeightPx,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            )
                        }
                    },
                    togglePlayPause = togglePlayPause,
                    playPauseScale = playPauseScale
                )

                // Expanded Player
                if (expansionFraction > 0.05f) {
                    ExpandedPlayer(
                        track = track,
                        sliderPosition = sliderPosition,
                        onSliderChange = onSliderChange,
                        onSliderChangeFinished = onSliderChangeFinished,
                        duration = duration,
                        artworkHeightDp = artworkHeightDp,
                        fadeInAlpha = fadeInAlpha,
                        isPlaying = isPlaying,
                        playPauseScale = playPauseScale,
                        playPrevious = playPrevious,
                        playNext = playNext,
                        togglePlayPause = togglePlayPause
                    )
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    track: Track,
    isPlaying: Boolean,
    minHeightDp: Dp,
    expanded: Boolean,
    onExpand: () -> Unit,
    togglePlayPause: () -> Unit,
    playPauseScale: Float
) {
    if (!expanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeightDp)
                .clickable { onExpand() }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.imageUrl,
                contentDescription = track.title,
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, style = MaterialTheme.typography.bodyLarge)
                Text(track.artist, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(
                onClick = togglePlayPause,
                modifier = Modifier.scale(playPauseScale)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayer(
    track: Track,
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit,
    duration: Long,
    artworkHeightDp: Dp,
    fadeInAlpha: Float,
    isPlaying: Boolean,
    playPauseScale: Float,
    playPrevious: () -> Unit,
    playNext: () -> Unit,
    togglePlayPause: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer { alpha = fadeInAlpha }
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Artwork Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(artworkHeightDp)
        ) {
            AsyncImage(
                model = track.imageUrl,
                contentDescription = track.title,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(track.title, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(2.dp))

        Text(track.artist, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Slider Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                formatTime((sliderPosition / 1000).toInt()),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )
            Slider(
                value = sliderPosition,
                onValueChange = { onSliderChange(it) },
                onValueChangeFinished = { onSliderChangeFinished() },
                valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                modifier = Modifier.weight(1f),
                thumb = { SliderDefaults.Thumb(interactionSource = remember { MutableInteractionSource() }) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                )
            )
            Text(
                formatTime((duration / 1000).toInt()),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controls
        Controls(
            isPlaying = isPlaying,
            playPauseScale = playPauseScale,
            togglePlayPause = togglePlayPause,
            playPrevious = playPrevious,
            playNext = playNext
        )
    }
}

@Composable
fun Controls(
    isPlaying: Boolean,
    playPauseScale: Float,
    togglePlayPause: () -> Unit,
    playPrevious: () -> Unit,
    playNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Previous Button
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = playPrevious,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface // Use theme color for contrast
                )
            }
        }

        // Play/Pause Button (Transparent background)
        IconButton(
            onClick = togglePlayPause,
            modifier = Modifier
                .size(72.dp)
                .scale(playPauseScale)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape) // CHANGED BACKGROUND
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant // CHANGED TINT
            )
        }

        // Next Button
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = playNext,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface // Use theme color for contrast
                )
            }
        }
    }
}

// Helper to format seconds into mm:ss
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(minutes, secs)
}