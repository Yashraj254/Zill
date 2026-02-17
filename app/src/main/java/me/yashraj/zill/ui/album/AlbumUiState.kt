package me.yashraj.zill.ui.album

import me.yashraj.zill.domain.model.Album

sealed interface AlbumUiState {
    object Loading : AlbumUiState
    data class Success(val albums: List<Album>) : AlbumUiState
    data class Error(val message: String) : AlbumUiState
}