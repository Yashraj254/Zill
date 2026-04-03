package me.yashraj.zill.ui.playlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.navigation.LocalPlayerSheetController
import me.yashraj.zill.ui.core.TrackUiState
import me.yashraj.zill.ui.music.MusicTrackItem
import me.yashraj.zill.ui.player.PlayerViewModel

@Composable
fun PlaylistDetailScreenContent(
    trackUiState: TrackUiState,
    onRemoveTrack: (Long) -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val sheetController = LocalPlayerSheetController.current
    var menuTrack by remember { mutableStateOf<Track?>(null) }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        when (trackUiState) {
            is TrackUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is TrackUiState.Success -> {
                if (trackUiState.tracks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tracks in this playlist yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(trackUiState.tracks, key = { _, track -> track.id }) { index, track ->
                        Box {
                            MusicTrackItem(
                                track = track,
                                onMoreClick = { menuTrack = track },
                                onClick = {
                                    playerViewModel.onPlayFromPlaylist(trackUiState.tracks, index)
                                    sheetController.show()
                                }
                            )
                            DropdownMenu(
                                expanded = menuTrack?.id == track.id,
                                onDismissRequest = { menuTrack = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Remove from playlist") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        onRemoveTrack(track.id)
                                        menuTrack = null
                                    }
                                )
                            }
                        }
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
