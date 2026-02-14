package me.yashraj.zill.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.repository.TrackRepository
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(trackRepository: TrackRepository) : ViewModel() {

    val folders: StateFlow<FolderUiState> = trackRepository.getTrackFolderPath()
        .map<List<Folder>, FolderUiState> { folders -> FolderUiState.Success(folders) }
        .onStart { emit(FolderUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FolderUiState.Loading
        )
}