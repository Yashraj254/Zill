package me.yashraj.zill.ui.artist

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
fun ArtistScreenContent(
    artistUiState: ArtistUiState,
    onArtistClick: (Long, String) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
        when (artistUiState) {
            is ArtistUiState.Loading -> {
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

            is ArtistUiState.Success -> {
                items(items = artistUiState.artists, key = { it.id }) { artist ->
                    ArtistItem(
                        artistName = artist.name,
                        albumCount = artist.albumCount,
                        songCount = artist.trackCount
                    ) {
                        onArtistClick(artist.id, artist.name)
                    }
                }
            }

            is ArtistUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = artistUiState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
