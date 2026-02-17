package me.yashraj.zill.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.model.Album
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.ui.core.TrackUiState
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(repository: TrackRepository) : ViewModel() {

    val albums: StateFlow<AlbumUiState> = repository.getAlbums()
        .map<List<Album>, AlbumUiState> { AlbumUiState.Success(it) }
        .onStart { emit(AlbumUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumUiState.Loading
        )

    private val _albumId: MutableStateFlow<Long?> = MutableStateFlow(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val albumTracks: StateFlow<TrackUiState> = _albumId.filterNotNull().flatMapLatest { albumId ->
        repository.getTracksByAlbum(albumId)
            .map<List<Track>, TrackUiState> { TrackUiState.Success(it) }
            .onStart { emit(TrackUiState.Loading) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrackUiState.Loading
    )

    fun getAlbumTracks(albumId: Long) {
        _albumId.value = albumId
    }
}