package me.yashraj.zill.ui.artist

import me.yashraj.zill.domain.model.Artist

sealed interface ArtistUiState {
    object Loading : ArtistUiState
    data class Success(val artists: List<Artist>) : ArtistUiState
    data class Error(val message: String) : ArtistUiState
}