package me.yashraj.zill.ui.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.yashraj.zill.navigation.LocalPlayerSheetController
import me.yashraj.zill.ui.core.TrackUiState
import me.yashraj.zill.ui.player.PlayerViewModel

@Composable
fun MusicScreenContent(
    trackUiState: TrackUiState,
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val state = rememberLazyListState()
    val sheetController = LocalPlayerSheetController.current

    LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
        when (trackUiState) {
            is TrackUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is TrackUiState.Success -> {
                itemsIndexed(items = trackUiState.tracks, key = { _, track -> track.id }) { index, track ->
                    MusicTrackItem(track) {
                        playerViewModel.onPlayFromPlaylist(trackUiState.tracks, index)
                        sheetController.show()
                    }
                }
            }

            is TrackUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trackUiState.error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}