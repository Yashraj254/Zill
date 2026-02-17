package me.yashraj.zill.ui.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

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
    }
}
