package me.yashraj.zill.ui.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.permission.PermissionType
import me.yashraj.zill.permission.components.RequestPermission
import me.yashraj.zill.permission.components.rememberPermissionState
import timber.log.Timber

@Composable
fun FolderScreen(onFolderClick: (String) -> Unit, viewModel: FolderViewModel = hiltViewModel()) {
    val appBar = LocalAppBarController.current

    DisposableEffect(Unit) {
        appBar.update { copy(title = "Folders", showBack = false, showSearch = true) }
        onDispose { appBar.clearSearch() }
    }

    var isPermissionGranted = rememberPermissionState(PermissionType.ReadAudio)
    RequestPermission(
        permissionType = PermissionType.ReadAudio,
        onPermissionGranted = {
            isPermissionGranted = true
        }
    )
    Timber.d("isPermissionGranted: %s", isPermissionGranted)

    if (isPermissionGranted) {

        val folders by viewModel.folders.collectAsStateWithLifecycle()
        val searchQuery = appBar.state.searchQuery

        Timber.d("folders: %s", folders)
        LaunchedEffect(searchQuery) {
            viewModel.onSearchFolder(searchQuery)
        }
        FolderScreenContent(folders) {
            onFolderClick(it)
        }
    }
}