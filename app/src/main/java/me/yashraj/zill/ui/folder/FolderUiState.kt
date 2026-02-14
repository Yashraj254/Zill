package me.yashraj.zill.ui.folder

import me.yashraj.zill.domain.model.Folder

sealed interface FolderUiState {
    object Loading : FolderUiState
    data class Success(val folders: List<Folder>) : FolderUiState
    data class Error(val error: String?) : FolderUiState
}