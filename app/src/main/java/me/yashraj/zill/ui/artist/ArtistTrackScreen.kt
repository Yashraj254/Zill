package me.yashraj.zill.ui.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.ui.music.MusicScreenContent

@Composable
fun ArtistTrackScreen(
    artistId: Long,
    artistName: String,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    DisposableEffect(artistId) {
        appBar.update { copy(title = artistName, showBack = true, showSearch = true) }
        viewModel.getArtistTracks(artistId)
        onDispose { appBar.clearSearch() }
    }

    val trackUiState by viewModel.artistTracks.collectAsStateWithLifecycle()
    LaunchedEffect(searchQuery) {
        viewModel.onSearchTrack(searchQuery)
    }
    MusicScreenContent(trackUiState)
}
