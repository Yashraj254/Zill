package me.yashraj.zill.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.ui.music.MusicScreenContent

@Composable
fun AlbumTrackScreen(
    albumId: Long,
    albumName: String,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    DisposableEffect(albumId) {
        appBar.update { copy(title = albumName, showBack = true, showSearch = true) }
        viewModel.getAlbumTracks(albumId)
        onDispose { appBar.clearSearch() }
    }

    val trackUiState by viewModel.albumTracks.collectAsStateWithLifecycle()
    LaunchedEffect(searchQuery) {
        viewModel.onSearchTrack(searchQuery)
    }
    MusicScreenContent(trackUiState)
}
