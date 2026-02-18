package me.yashraj.zill.ui.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.ui.core.MusicTrackViewModel
import me.yashraj.zill.ui.music.MusicScreenContent

@Composable
fun FolderTrackScreen(
    folderPath: String,
    viewModel: FolderViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    DisposableEffect(folderPath) {
        appBar.update { copy(title = folderPath.takeLastWhile { it != '/' }, showBack = true, showSearch = true) }
        viewModel.getFolderTracks(folderPath)
        onDispose { appBar.clearSearch() }
    }

    val trackUiState by viewModel.folderTracks.collectAsStateWithLifecycle()
    LaunchedEffect(searchQuery) {
        viewModel.onSearchTrack(searchQuery)
    }
    MusicScreenContent(trackUiState)
}

