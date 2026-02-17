package me.yashraj.zill.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.permission.PermissionType
import me.yashraj.zill.permission.components.RequestPermission
import me.yashraj.zill.permission.components.rememberPermissionState
import timber.log.Timber

@Composable
fun AlbumScreen(onAlbumClick: (Long, String) -> Unit) {
    val appBar = LocalAppBarController.current

    LaunchedEffect(Unit) {
        appBar.update {
            copy(
                title = "Albums",
                showBack = false
            )
        }
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
        val viewModel: AlbumViewModel = hiltViewModel()
        val albums by viewModel.albums.collectAsStateWithLifecycle()
        Timber.d("albums: %s", albums)
        AlbumScreenContent(albums) { id, name ->
            onAlbumClick(id, name)
        }
    }
}
