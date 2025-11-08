package com.musicplayer.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musicplayer.network.Track
import com.musicplayer.presentation.viewModels.PlayerViewModel
import com.musicplayer.presentation.viewModels.SearchViewModel

@Composable
fun SearchHostScreen(
    playerViewModel: PlayerViewModel,
    viewModel: SearchViewModel = hiltViewModel(),
    onThemeSelected: (AppTheme) -> Unit,
    onNavigateBack: () -> Unit = {},
    onMenuClick: () -> Unit // pass drawer toggle from NavGraph
) {
    val context = LocalContext.current
    playerViewModel.initPlayer(context)

    val tracks by viewModel.filteredTracks.collectAsStateWithLifecycle()

    // Pass onMenuClick down to SearchScreen
    SearchScreen(
        playerViewModel = playerViewModel,
        viewModel = viewModel,
        onThemeSelected = onThemeSelected,
        onNavigateBack = onNavigateBack,
        onTrackSelected = { track ->
            val index = tracks.indexOf(track)
            if (index != -1) {
                playerViewModel.playTrack(context, tracks, index)
            }
        },
        onMenuClick = onMenuClick // drawer toggle
    )
}