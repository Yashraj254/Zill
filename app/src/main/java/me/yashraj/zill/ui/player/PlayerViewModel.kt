package me.yashraj.zill.ui.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.yashraj.zill.background.PlayerManager
import me.yashraj.zill.domain.model.Track
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerManager: PlayerManager,
    private val trackRepository: me.yashraj.zill.domain.repository.TrackRepository
) : ViewModel() {

    private val _openExpanded = MutableStateFlow(false)
    val openExpanded = _openExpanded.asStateFlow()

    val uiState: StateFlow<PlayerUiState> = playerManager.uiState

    fun togglePlayPause() = playerManager.playPause()
    fun skipNext() = playerManager.next()
    fun skipPrevious() = playerManager.previous()
    fun seekTo(ms: Long) = playerManager.seek(ms)

    fun onPlayFromPlaylist(tracks: List<Track>, startIndex: Int) = viewModelScope.launch {
        playerManager.playFromPlaylist(tracks, startIndex)
    }

    fun requestOpenExpanded() {
        _openExpanded.value = true
    }

    fun consumeOpenExpanded() {
        _openExpanded.value = false
    }

    fun playFromUri(uri: Uri) = viewModelScope.launch {
        Timber.d("playFromUri: $uri")

        // Resolve the tapped file to a Track
        val tappedTrack = trackRepository.getTrackByUri(uri)
            ?: buildMinimalTrackFromUri(uri)

        // Load all tracks from the same directory
        val directoryPath = File(tappedTrack.path).parent

        val tracks: List<Track> = if (!directoryPath.isNullOrBlank()) {
            trackRepository.getFolderTracks(directoryPath).firstOrNull()
                ?: listOf(tappedTrack)      // folder scan failed, just play the one file
        } else {
            listOf(tappedTrack)             // no path info at all, just play the one file
        }

        // Find the tapped track in the folder list so it plays first
        val startIndex = tracks
            .indexOfFirst { it.id == tappedTrack.id && it.path == tappedTrack.path }
            .takeIf { it >= 0 } ?: 0

        Timber.d("playFromUri: playing index $startIndex of ${tracks.size} tracks in $directoryPath")

        playerManager.playFromPlaylist(tracks, startIndex)
        _openExpanded.value = true
    }

    private fun buildMinimalTrackFromUri(uri: Uri): Track {
        val fileName = uri.lastPathSegment
            ?.substringAfterLast('/')
            ?.substringBeforeLast('.')
            ?: "Unknown"

        return Track(
            id = 0L,
            uri = uri,
            title = fileName,
            artist = "Unknown Artist",
            album = "Unknown Album",
            albumId = 0L,
            artworkUri = Uri.EMPTY,
            duration = 0L,
            path = uri.path ?: "",
            dateAdded = 0L,
            dateModified = 0L,
            size = 0L,
            mimeType = "audio/*",
            trackNumber = null,
            year = null
        )
    }

}
