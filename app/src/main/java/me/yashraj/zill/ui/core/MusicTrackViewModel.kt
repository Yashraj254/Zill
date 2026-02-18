package me.yashraj.zill.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.repository.TrackRepository
import javax.inject.Inject

@HiltViewModel
class MusicTrackViewModel @Inject constructor(trackRepository: TrackRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")


    val tracks: StateFlow<TrackUiState> = combine(
        trackRepository.getAllTracks(),
        _searchQuery
    ) { tracks, query ->
        val filtered = if (query.isBlank()) tracks
        else tracks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true)
        }
        TrackUiState.Success(filtered)
    }
        .map<TrackUiState, TrackUiState> { it }
        .onStart { emit(TrackUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrackUiState.Loading
        )


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}