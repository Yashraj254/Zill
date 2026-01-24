package me.yashraj.zill.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import javax.inject.Inject

@HiltViewModel
class MusicTrackViewModel @Inject constructor(private val trackRepository: TrackRepository) : ViewModel() {

    val tracks: StateFlow<TrackUiState> = trackRepository.getAllTracks()
        .map<List<Track>, TrackUiState> { TrackUiState.Success(it) }
        .onStart { emit(TrackUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrackUiState.Loading
        )


}