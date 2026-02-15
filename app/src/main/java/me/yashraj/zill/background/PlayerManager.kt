package me.yashraj.zill.background

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import me.yashraj.zill.domain.mapper.toMediaItem
import me.yashraj.zill.ui.player.PlayerUiState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackRepository: TrackRepository
) {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var progressJob: Job? = null

    // Uses Main.immediate so UI updates are applied synchronously when already on the main thread,
    // avoiding a frame of lag on rapid state changes (e.g. seek bar dragging).
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val isConnected: Boolean
        get() = controller?.isConnected == true

    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgress() else stopProgress()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val ctrl = controller ?: return
            val index = ctrl.currentMediaItemIndex

            // Read currentSong from the cached playlist rather than doing a DB lookup here,
            // since this callback can fire rapidly (e.g. during queue shuffles).
            _uiState.update {
                it.copy(
                    currentIndex = index,
                    currentSong = uiState.value.playlist.getOrNull(index),
                    progressMs = 0L,
                    durationMs = ctrl.duration.coerceAtLeast(0L)
                )
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            // Duration is only reliable once the player has buffered enough to know the track length.
            if (state == Player.STATE_READY) {
                _uiState.update {
                    it.copy(durationMs = controller?.duration ?: 0L)
                }
            }
        }
    }

    /**
     * Binds to [PlayerService] via a [MediaController].
     */
    fun connect() {
        if (isConnected) return

        val token = SessionToken(
            context,
            ComponentName(context, PlayerService::class.java)
        )

        controllerFuture = MediaController.Builder(context, token)
            .buildAsync()
            .also { future ->
                future.addListener(
                    { onControllerReady(future) },
                    MoreExecutors.directExecutor()
                )
            }
    }

    private fun onControllerReady(future: ListenableFuture<MediaController>) {
        val ctrl = runCatching { future.get() }.getOrNull() ?: return

        controller = ctrl
        ctrl.addListener(playerListener)

        // Rebuild the in-memory playlist by resolving each MediaItem's mediaId back to a Track.
        // This re-sync is necessary when the service was already running before the UI connected
        // (e.g. app process was killed and restarted while music was playing).
        scope.launch {
            val index = ctrl.currentMediaItemIndex
            val tracks = (0 until ctrl.mediaItemCount).mapNotNull { i ->
                ctrl.getMediaItemAt(i).mediaId
                    .let { id -> trackRepository.getTrackById(id.toLongOrNull()) }
            }

            _uiState.update {
                it.copy(
                    isConnected = true,
                    isPlaying = ctrl.isPlaying,
                    currentIndex = index,
                    currentSong = tracks.getOrNull(index),
                    playlist = tracks,
                    durationMs = ctrl.duration.coerceAtLeast(0L),
                    progressMs = ctrl.currentPosition.coerceAtLeast(0L)
                )
            }

            if (ctrl.isPlaying) startProgress()
        }
    }

    /**
     * Suspends until a live [MediaController] is available.
     *
     * Handles the case where the service was killed (e.g. swiped from recents) —
     * stale references are cleaned up, [connect] is called to restart the service,
     * and we await the new controller before proceeding.
     */
    private suspend fun awaitController(): MediaController {
        controller?.takeIf { it.isConnected }?.let { return it }

        controller?.removeListener(playerListener)
        controllerFuture?.let(MediaController::releaseFuture)
        controller = null
        controllerFuture = null

        connect()

        return controllerFuture!!.await()
    }

    fun playPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seek(ms: Long) = controller?.seekTo(ms)
    fun next() = controller?.seekToNextMediaItem()
    fun previous() = controller?.seekToPreviousMediaItem()

    private fun startProgress() {
        // Cancel any existing job before relaunching to avoid two coroutines polling simultaneously.
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val pos = controller?.takeIf { it.isPlaying }?.currentPosition
                if (pos != null) _uiState.update { it.copy(progressMs = pos) }
                delay(500)
            }
        }
    }

    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    /**
     * Replaces the current queue with [tracks] and starts playback from [startIndex].
     */
    suspend fun playFromPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        val ctrl = awaitController()

        _uiState.update {
            it.copy(
                playlist = tracks,
                currentIndex = startIndex,
                currentSong = tracks.getOrNull(startIndex),
                progressMs = 0L
            )
        }

        val mediaItems = tracks.map { track ->
            track.toMediaItem()
        }

        ctrl.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
        ctrl.prepare()
        ctrl.play()
    }

}
