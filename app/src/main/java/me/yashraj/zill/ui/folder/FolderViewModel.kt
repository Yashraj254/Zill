package me.yashraj.zill.ui.folder

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
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.ui.core.TrackUiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(trackRepository: TrackRepository) : ViewModel() {

    private val _searchFolderQuery = MutableStateFlow("")
    private val _searchTrackQuery = MutableStateFlow("")
    private val _folderPath: MutableStateFlow<String?> = MutableStateFlow(null)

    val folders: StateFlow<FolderUiState> = combine(
        trackRepository.getTrackFolderPath(),
        _searchFolderQuery
    ) { folders, query ->
        Timber.d("Filtering folders with query: '$query'")
        val filtered = if (query.isBlank()) folders
        else folders.filter {
            it.name.contains(query, ignoreCase = true)
        }
        FolderUiState.Success(filtered)
    }
        .map<FolderUiState, FolderUiState> { it }
        .onStart { emit(FolderUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FolderUiState.Loading
        )


    val folderTracks: StateFlow<TrackUiState> = combine(
        _folderPath.filterNotNull().flatMapLatest { path ->
            trackRepository.getFolderTracks(path)
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

    fun getFolderTracks(path: String) {
        _folderPath.value = path
    }

    fun onSearchTrack(query: String) {
        _searchTrackQuery.value = query
    }

    fun onSearchFolder(query: String) {
        _searchFolderQuery.value = query
    }
}