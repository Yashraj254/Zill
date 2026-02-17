package me.yashraj.zill.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.ui.core.TrackUiState
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(repository: TrackRepository) : ViewModel() {

    val artists: StateFlow<ArtistUiState> = repository.getArtists()
        .map<List<me.yashraj.zill.domain.model.Artist>, ArtistUiState> { ArtistUiState.Success(it) }
        .onStart { emit(ArtistUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistUiState.Loading
        )

    private val _artistId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val artistTracks: StateFlow<TrackUiState> = _artistId.filterNotNull().flatMapLatest { artistId ->
        repository.getTracksByArtist(artistId)
            .map<List<Track>, TrackUiState> { TrackUiState.Success(it) }
            .onStart { emit(TrackUiState.Loading) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrackUiState.Loading
    )

    fun getArtistTracks(artistId: Long) {
        _artistId.value = artistId
    }

}