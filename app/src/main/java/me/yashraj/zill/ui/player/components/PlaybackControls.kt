package me.yashraj.zill.ui.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.yashraj.zill.ui.player.LoopMode
import me.yashraj.zill.ui.theme.ControlSecondary
import me.yashraj.zill.ui.theme.IcyAccent

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    loopMode: LoopMode,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLoop: () -> Unit,
    onAddToPlaylist: () -> Unit,
    addToPlaylistEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

        ControlIconButton(
            icon = when (loopMode) {
                LoopMode.ONE -> Icons.Default.RepeatOne
                else -> Icons.Default.Repeat
            },
            contentDescription = "Loop: ${loopMode.name}",
            onClick = onToggleLoop,
            tint = if (loopMode == LoopMode.OFF) ControlSecondary else IcyAccent
        )

        ControlIconButton(
            icon = Icons.Default.SkipPrevious,
            contentDescription = "Previous",
            onClick = onPrevious
        )

        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayPause
        )

        ControlIconButton(
            icon = Icons.Default.SkipNext,
            contentDescription = "Next",
            onClick = onNext
        )

        ControlIconButton(
            icon = Icons.Default.PlaylistAdd,
            contentDescription = "Add to playlist",
            onClick = onAddToPlaylist,
            enabled = addToPlaylistEnabled,
            tint = ControlSecondary,
        )
    }
}
