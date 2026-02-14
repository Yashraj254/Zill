package me.yashraj.zill.ui.core

import me.yashraj.zill.domain.model.Track

sealed interface TrackUiState {
    object Loading : TrackUiState
    data class Success(val tracks: List<Track>) : TrackUiState
    data class Error(val error: String?) : TrackUiState
}