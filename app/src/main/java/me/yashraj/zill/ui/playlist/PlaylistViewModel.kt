package me.yashraj.zill.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.yashraj.zill.domain.repository.PlaylistRepository
import me.yashraj.zill.ui.core.TrackUiState
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _currentPlaylistId = MutableStateFlow<Long?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    val playlists: StateFlow<PlaylistUiState> = playlistRepository.getAllPlaylists()
        .map { PlaylistUiState.Success(it) as PlaylistUiState }
        .onStart { emit(PlaylistUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistUiState.Loading
        )

    val playlistTracks: StateFlow<TrackUiState> = combine(
        _currentPlaylistId.filterNotNull().flatMapLatest { id ->
            playlistRepository.getPlaylistTracks(id)
        },
        _searchQuery
    ) { tracks, query ->
        val filtered = if (query.isBlank()) tracks
        else tracks.filter { it.title.contains(query, ignoreCase = true) }
        TrackUiState.Success(filtered) as TrackUiState
    }
        .onStart { emit(TrackUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrackUiState.Loading
        )

    fun loadPlaylistTracks(playlistId: Long) {
        _currentPlaylistId.value = playlistId
    }

    fun onSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun nameExists(name: String): Boolean {
        val state = playlists.value
        return state is PlaylistUiState.Success &&
                state.playlists.any { it.name.equals(name, ignoreCase = true) }
    }

    fun createPlaylist(name: String) = viewModelScope.launch {
        if (nameExists(name)) {
            _toastMessage.emit("A playlist named \"$name\" already exists")
            return@launch
        }
        playlistRepository.createPlaylist(name)
    }

    fun deletePlaylist(playlistId: Long) = viewModelScope.launch {
        playlistRepository.deletePlaylist(playlistId)
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) = viewModelScope.launch {
        playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) = viewModelScope.launch {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    fun createPlaylistAndAddTrack(name: String, trackId: Long) = viewModelScope.launch {
        if (nameExists(name)) {
            _toastMessage.emit("A playlist named \"$name\" already exists")
            return@launch
        }
        playlistRepository.createPlaylistAndAddTrack(name, trackId)
    }
}
