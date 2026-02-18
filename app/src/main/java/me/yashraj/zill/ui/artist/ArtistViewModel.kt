package me.yashraj.zill.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.ui.album.AlbumUiState
import me.yashraj.zill.ui.core.TrackUiState
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(repository: TrackRepository) : ViewModel() {

    private val _artistId: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val _searchArtistQuery = MutableStateFlow("")
    private val _searchTrackQuery = MutableStateFlow("")

    val artists: StateFlow<ArtistUiState> = combine(
        repository.getArtists(),
        _searchArtistQuery
    ) { artists, query ->
        val filtered = if (query.isBlank()) artists
        else artists.filter {
            it.name.contains(query, ignoreCase = true)
        }
        ArtistUiState.Success(filtered)
    }
        .map<ArtistUiState, ArtistUiState> { it }
        .onStart { emit(ArtistUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistUiState.Loading
        )

    val artistTracks: StateFlow<TrackUiState> = combine(
        _artistId.filterNotNull().flatMapLatest { id ->
            repository.getTracksByArtist(id)
        },
        _searchTrackQuery
    ) { tracks, query ->
        val filtered = if (query.isBlank()) tracks
        else tracks.filter {
            it.title.contains(query, ignoreCase = true)
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

    fun getArtistTracks(artistId: Long) {
        _artistId.value = artistId
    }

    fun onSearchTrack(query: String) {
        _searchTrackQuery.value = query
    }

    fun onSearchArtist(query: String) {
        _searchArtistQuery.value = query
    }
}