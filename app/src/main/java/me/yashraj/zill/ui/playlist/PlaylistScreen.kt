package me.yashraj.zill.ui.playlist

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController

@Composable
fun PlaylistScreen(
    onPlaylistClick: (Long, String) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    LaunchedEffect(searchQuery) {
        viewModel.onSearchPlaylist(searchQuery)
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    PlaylistScreenContent(
        uiState = playlists,
        onPlaylistClick = onPlaylistClick,
        onDeletePlaylist = { viewModel.deletePlaylist(it) },
        onCreatePlaylist = { viewModel.createPlaylist(it) }
    )
}
