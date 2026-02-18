package me.yashraj.zill.ui.artist

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
fun ArtistScreen(onArtistClick: (Long, String) -> Unit, viewModel: ArtistViewModel = hiltViewModel()) {
    val appBar = LocalAppBarController.current
    val searchQuery = appBar.state.searchQuery

    DisposableEffect(Unit) {
        appBar.update { copy(title = "Artists", showBack = false, showSearch = true) }
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
        val artists by viewModel.artists.collectAsStateWithLifecycle()
        Timber.d("artists: %s", artists)
        LaunchedEffect(searchQuery) {
            viewModel.onSearchArtist(searchQuery)
        }
        ArtistScreenContent(artists) { id, name ->
            onArtistClick(id, name)
        }
    }
}
