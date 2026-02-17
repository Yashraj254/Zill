package me.yashraj.zill.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.yashraj.zill.background.PlayerManager
import me.yashraj.zill.domain.model.Track
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerManager: PlayerManager
) : ViewModel() {

    val uiState: StateFlow<PlayerUiState> = playerManager.uiState

    fun togglePlayPause() = playerManager.playPause()
    fun skipNext() = playerManager.next()
    fun skipPrevious() = playerManager.previous()
    fun seekTo(ms: Long) = playerManager.seek(ms)

    fun onPlayFromPlaylist(tracks: List<Track>, startIndex: Int) = viewModelScope.launch {
        playerManager.playFromPlaylist(tracks, startIndex)
    }
}
