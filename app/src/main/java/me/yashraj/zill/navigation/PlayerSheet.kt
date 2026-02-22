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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yashraj.zill.ui.player.MiniPlayer
import me.yashraj.zill.ui.player.MusicPlayerScreen
import me.yashraj.zill.ui.player.PlayerViewModel


val MINI_PLAYER_HEIGHT = 72.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerDraggableSheet(
    draggableState: AnchoredDraggableState<PlayerSheetState>,
    viewModel: PlayerViewModel = hiltViewModel(),
    onExpand: () -> Unit,
) {
    val density = LocalDensity.current

    val playerState by viewModel.uiState.collectAsStateWithLifecycle()

    // Expansion progress (0f = MINI, 1f = EXPANDED)
    val expandProgress by remember(draggableState) {
        derivedStateOf {
            val anchors = draggableState.anchors
            val mini = anchors.positionOf(PlayerSheetState.MINI)
            val expanded = anchors.positionOf(PlayerSheetState.EXPANDED)

            if (mini == expanded || mini.isNaN() || expanded.isNaN()) {
                0f
            } else {
                val raw =
                    1f - (draggableState.offset - expanded) / (mini - expanded)
                raw.coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Vertical,
            )
            .graphicsLayer {
                // Vertical movement
                translationY = draggableState
                    .requireOffset()
                    .let { if (it.isNaN()) 0f else it }

                // Corner radius (20dp → 0dp) in pixels
                val radiusPx = with(density) {
                    lerp(20.dp, 0.dp, expandProgress).toPx()
                }

                shape = RoundedCornerShape(topStart = radiusPx, topEnd = radiusPx)
                clip = true
            }
            .background(Color(0xFF0D1017))
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = expandProgress }
        ) {
            MusicPlayerScreen(
                state = playerState,
                onPlayPause = viewModel::togglePlayPause,
                onPrevious = viewModel::skipPrevious,
                onNext = viewModel::skipNext,
                onSeek = viewModel::seekTo,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MINI_PLAYER_HEIGHT)
                .align(Alignment.TopCenter)
                .graphicsLayer { alpha = 1f - expandProgress }
        ) {
            MiniPlayer(
                state = playerState,
                onPlayPause = viewModel::togglePlayPause,
                onClick = { onExpand() },
            )
        }

        // Drag Handle
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .size(width = 36.dp, height = 4.dp)
                .graphicsLayer { alpha = expandProgress }
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
        )
    }
}


