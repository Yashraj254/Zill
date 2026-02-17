package me.yashraj.zill.ui.folder.tracks

import androidx.compose.runtime.Composable
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
    viewModel: MusicTrackViewModel = hiltViewModel()
) {
    val appBar = LocalAppBarController.current

    LaunchedEffect(Unit) {
        appBar.update {
            copy(
                title = folderPath.takeLastWhile { it != '/' },
                showBack = true
            )
        }
    }
    LaunchedEffect(folderPath) {
        viewModel.getFolderTracks(folderPath)
    }
    val trackUiState by viewModel.folderTracks.collectAsStateWithLifecycle()
    MusicScreenContent(trackUiState)
}

