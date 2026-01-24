package me.yashraj.zill.ui.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.ui.LocalAppBarController
import me.yashraj.zill.ui.permission.PermissionType
import me.yashraj.zill.ui.permission.components.RequestPermission
import me.yashraj.zill.ui.permission.components.rememberPermissionState
import timber.log.Timber

@Composable
fun MusicScreen() {
    val appBar = LocalAppBarController.current

    LaunchedEffect(Unit) {
        appBar.update {
            copy(
                title = "Music",
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
        val viewModel: MusicTrackViewModel = hiltViewModel()
        val tracks by viewModel.tracks.collectAsStateWithLifecycle()
        Timber.d("tracks: %s", tracks)

        
        MusicScreenContent(tracks)
    }
}