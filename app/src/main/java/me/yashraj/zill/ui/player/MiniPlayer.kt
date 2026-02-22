package me.yashraj.zill.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.yashraj.zill.R
import me.yashraj.zill.ui.theme.ControlAccentEnd
import me.yashraj.zill.ui.theme.ControlAccentStart
import me.yashraj.zill.ui.theme.IcyAccent
import me.yashraj.zill.ui.theme.IcyBgBottom
import me.yashraj.zill.ui.theme.IcyBgTop
import me.yashraj.zill.ui.theme.IcyPrimary
import me.yashraj.zill.ui.theme.IcySecondary
import me.yashraj.zill.ui.theme.IcySurface

@Composable
fun MiniPlayer(
    state: PlayerUiState,
    onPlayPause: () -> Unit,
    onClick: () -> Unit,
) {
    val progress = (state.progressMs.toFloat() / state.durationMs.coerceAtLeast(1L))
        .coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        IcyBgTop,
                        IcyBgBottom
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(1.5.dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            IcyAccent.copy(alpha = 0.9f),
                            IcyAccent.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // Artwork
            AsyncImage(
                model = state.currentTrack?.artworkUri,
                contentDescription = "Mini player artwork",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.zill_logo),
                error = painterResource(R.drawable.zill_logo),
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IcySurface)
            )

            Spacer(Modifier.width(14.dp))

            // Title & artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.currentTrack?.title ?: "Unknown Title",
                    color = IcyPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.currentTrack?.artist ?: "Unknown Artist",
                    color = IcySecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Play / Pause
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ControlAccentStart,
                                ControlAccentEnd
                            )
                        )
                    )
                    .clickable(onClick = onPlayPause),
            ) {
                Icon(
                    imageVector = if (state.isPlaying)
                        Icons.Default.Pause
                    else
                        Icons.Default.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next track",
                tint = IcySecondary,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF080A12, widthDp = 390, heightDp = 72)
@Composable
private fun MiniPlayerPreview() {
    MiniPlayer(
        state = PlayerUiState(
            currentTrack = sampleTrack,
            isPlaying = true,
            progressMs = 80_000L,
        ),
        onPlayPause = {},
        onClick = {},
    )
}