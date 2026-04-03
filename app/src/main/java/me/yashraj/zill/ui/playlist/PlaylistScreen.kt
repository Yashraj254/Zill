package me.yashraj.zill.ui.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlaylistScreen(
    onPlaylistClick: (Long, String) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    PlaylistScreenContent(
        uiState = playlists,
        onPlaylistClick = onPlaylistClick,
        onDeletePlaylist = { viewModel.deletePlaylist(it) },
        onCreatePlaylist = { viewModel.createPlaylist(it) }
    )
}
