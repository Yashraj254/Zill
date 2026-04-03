package me.yashraj.zill.ui.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylistTracks(playlistId)
    }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQuery(searchQuery)
    }

    val trackUiState by viewModel.playlistTracks.collectAsStateWithLifecycle()

    PlaylistDetailScreenContent(
        trackUiState = trackUiState,
        onRemoveTrack = { trackId -> viewModel.removeTrackFromPlaylist(playlistId, trackId) }
    )
}
