package me.yashraj.zill.ui.folder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FolderScreenContent(
    folderUiState: FolderUiState, onFolderClick: (String) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
        when (folderUiState) {
            is FolderUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is FolderUiState.Success -> {
                items(items = folderUiState.folders, key = { it.path }) { folder ->
                    FolderItem(folder.name, folder.trackCount) {
                        onFolderClick(folder.path)
                    }
                }
            }

            is FolderUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = folderUiState.error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}