package me.yashraj.zill.ui.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.navigation.LocalAppBarController
import me.yashraj.zill.permission.PermissionType
import me.yashraj.zill.permission.components.RequestPermission
import me.yashraj.zill.permission.components.rememberPermissionState
import me.yashraj.zill.ui.core.MusicTrackViewModel
import timber.log.Timber

@Composable
fun MusicScreen(viewModel: MusicTrackViewModel = hiltViewModel()) {
    val appBar = LocalAppBarController.current

    DisposableEffect(Unit) {
        appBar.update { copy(title = "Music", showBack = false, showSearch = true) }
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
        val tracks by viewModel.tracks.collectAsStateWithLifecycle()
        val searchQuery = appBar.state.searchQuery
        Timber.d("tracks: %s", tracks)
        LaunchedEffect(searchQuery) {
            viewModel.onSearchQueryChange(searchQuery)
        }
        MusicScreenContent(tracks)
    }
}