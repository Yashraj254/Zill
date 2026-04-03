package me.yashraj.zill.ui.playlist

import me.yashraj.zill.domain.model.Playlist

sealed interface PlaylistUiState {
    object Loading : PlaylistUiState
    data class Success(val playlists: List<Playlist>) : PlaylistUiState
    data class Error(val message: String) : PlaylistUiState
}
