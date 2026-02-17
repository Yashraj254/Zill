package me.yashraj.zill.ui.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlbumScreenContent(
    albumUiState: AlbumUiState,
    onAlbumClick: (Long, String) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
        when (albumUiState) {
            is AlbumUiState.Loading -> {
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

            is AlbumUiState.Success -> {
                items(items = albumUiState.albums, key = { it.id }) { album ->
                    AlbumItem(
                        albumName = album.title,
                        artistName = album.artistName,
                        songCount = album.trackCount
                    ) {
                        onAlbumClick(album.id, album.title)
                    }
                }
            }

            is AlbumUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = albumUiState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
