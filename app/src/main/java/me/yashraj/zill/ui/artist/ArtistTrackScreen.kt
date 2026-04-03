package me.yashraj.zill.ui.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.ui.music.MusicScreenContent

@Composable
fun ArtistTrackScreen(
    artistId: Long,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    LaunchedEffect(artistId) {
        viewModel.getArtistTracks(artistId)
    }

    val trackUiState by viewModel.artistTracks.collectAsStateWithLifecycle()
    LaunchedEffect(searchQuery) {
        viewModel.onSearchTrack(searchQuery)
    }
    MusicScreenContent(trackUiState)
}
