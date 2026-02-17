package me.yashraj.zill.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.ui.player.MiniPlayer
import me.yashraj.zill.ui.player.MusicPlayerScreen
import me.yashraj.zill.ui.player.PlayerUiState
import me.yashraj.zill.ui.player.PlayerViewModel
import me.yashraj.zill.ui.player.sampleTrack

// ─────────────────────────────────────────────────────────────────────────────
// DRAGGABLE SHEET CONTAINER
// ─────────────────────────────────────────────────────────────────────────────

val MINI_PLAYER_HEIGHT = 72.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerDraggableSheet(
    draggableState: AnchoredDraggableState<PlayerSheetState>,
    expandProgress: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Corner radius: 20dp when mini → 0dp when fully expanded
    val cornerRadius by remember {
        derivedStateOf { androidx.compose.ui.unit.lerp(20.dp, 0.dp, expandProgress) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Translate the whole sheet vertically according to drag offset
            .graphicsLayer {
                translationY = draggableState
                    .requireOffset()
                    .let { if (it.isNaN()) 0f else it }
            }
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Vertical,
            )
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(Color(0xFF0D1017)),
    ) {
        // ── Full Player (fades in as we expand) ──────────────────────────────
        if (expandProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = expandProgress },
            ) {
                // Your MusicPlayerScreen composable goes here.
                // We pass a placeholder that reads from your real PlayerState / ViewModel.
                FullPlayerContent(
                    expandProgress = expandProgress,
                    onDismiss = onCollapse,
                )
            }
        }

        // ── Mini Player (fades out as we expand) ─────────────────────────────
        if (expandProgress < 0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MINI_PLAYER_HEIGHT)
                    .align(Alignment.TopCenter)
                    .graphicsLayer { alpha = 1f - expandProgress },
            ) {
                MiniPlayerContent(
                    onClick = onExpand,
                )
            }
        }

        // ── Drag handle pill ─────────────────────────────────────────────────
        if (expandProgress > 0.02f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f * expandProgress)),
            )
        }
    }
}


@Composable
private fun FullPlayerContent(
    expandProgress: Float,
    onDismiss: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    // Wire your actual MusicPlayerScreen here, e.g.:
    //
       val playerState by viewModel.uiState.collectAsStateWithLifecycle()
       MusicPlayerScreen(
           state       = playerState,
           onPlayPause = viewModel::togglePlayPause,
           onPrevious  = viewModel::skipPrevious,
           onNext      = viewModel::skipNext,
           onSeek      = viewModel::seekTo,
       )

    // For now we render the same MusicPlayerScreen from the previous file:
//    MusicPlayerScreen(
//        state = rememberSamplePlayerState(),
//        onPlayPause = {},
//        onPrevious = {},
//        onNext = {},
//        onSeek = {},
//        onShuffle = {},
//        onRepeat = {},
//        onFavorite = {},
//        onDismiss = onDismiss,
//    )
}

// ─────────────────────────────────────────────────────────────────────────────
// MINI PLAYER PLACEHOLDER
// Replace the body with your real MiniPlayer composable.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MiniPlayerContent(onClick: () -> Unit,    viewModel: PlayerViewModel = hiltViewModel()
) {
    // Wire your real MiniPlayer here, e.g.:
    //
       val playerState by viewModel.uiState.collectAsStateWithLifecycle()
       MiniPlayer(
           state       = playerState,
           onPlayPause = viewModel::togglePlayPause,
           onClick     = onClick,
       )
//    MiniPlayer(
//        state = rememberSamplePlayerState(),
//        onPlayPause = {},
//        onClick = onClick,
//    )
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

/** Quick sample state for previews / placeholders. */
@Composable
private fun rememberSamplePlayerState(): PlayerUiState = remember {
    PlayerUiState(
        currentTrack = sampleTrack,
        isPlaying = true,
        progressMs = 62_000L,
    )
}

/** Linearly interpolate between two Dp values. */
fun lerp(start: androidx.compose.ui.unit.Dp, stop: androidx.compose.ui.unit.Dp, fraction: Float): androidx.compose.ui.unit.Dp =
    androidx.compose.ui.unit.Dp(start.value + (stop.value - start.value) * fraction)
