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
    artistName: String,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current

    LaunchedEffect(artistId) {
        viewModel.getArtistTracks(artistId)
    }

    val trackUiState by viewModel.artistTracks.collectAsStateWithLifecycle()

    LaunchedEffect(trackUiState) {
        appBar.update {
            copy(
                title = artistName,
                showBack = true
            )
        }
    }

    MusicScreenContent(trackUiState)
}
