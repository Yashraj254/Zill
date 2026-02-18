package me.yashraj.zill.ui.album

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
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.ui.core.TrackUiState
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(repository: TrackRepository) : ViewModel() {

    private val _albumId: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val _searchAlbumQuery = MutableStateFlow("")
    private val _searchTrackQuery = MutableStateFlow("")

    val albums: StateFlow<AlbumUiState> = combine(
        repository.getAlbums(),
        _searchAlbumQuery
    ) { albums, query ->
        val filtered = if (query.isBlank()) albums
        else albums.filter {
            it.title.contains(query, ignoreCase = true)
        }
        AlbumUiState.Success(filtered)
    }
        .map<AlbumUiState, AlbumUiState> { it }
        .onStart { emit(AlbumUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumUiState.Loading
        )

    val albumTracks: StateFlow<TrackUiState> = combine(
        _albumId.filterNotNull().flatMapLatest { id ->
            repository.getTracksByAlbum(id)
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

    fun getAlbumTracks(albumId: Long) {
        _albumId.value = albumId
    }

    fun onSearchTrack(query: String) {
        _searchTrackQuery.value = query
    }

    fun onSearchAlbum(query: String) {
        _searchAlbumQuery.value = query
    }
}