package me.yashraj.zill.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import me.yashraj.zill.R
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.ui.player.components.MusicSeekBar
import me.yashraj.zill.ui.player.components.PlaybackControls
import me.yashraj.zill.ui.theme.IcyBgBottom
import me.yashraj.zill.ui.theme.IcyBgTop
import me.yashraj.zill.ui.theme.IcyPrimary
import me.yashraj.zill.ui.theme.IcySecondary
import me.yashraj.zill.ui.theme.IcySurface


val sampleTrack = Track(
    id = 1L,
    title = "Midnight Reverie",
    artist = "Luna Voss",
    artworkUri = "https://picsum.photos/seed/music1/600/600".toUri(),
    duration = 237_000L,
    album = "Dreamscapes",
    albumId = 1L,
    path = "/storage/emulated/0/Music/midnight_reverie.mp3",
    dateAdded = 1620000000000L,
    dateModified = 1620000000000L,
    size = 5_000_000L,
    mimeType = "audio/mpeg",
    trackNumber = 1,
    year = 2021,
    uri = "".toUri()
)


@Composable
fun MusicPlayerScreen(
    state: PlayerUiState,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(IcyBgTop, IcyBgBottom)
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(Modifier.height(48.dp))

            // Artwork card (soft surface gradient)
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                IcySurface,
                                IcySurface.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                AsyncImage(
                    model = state.currentTrack?.artworkUri,
                    placeholder = painterResource(R.drawable.zill),
                    error = painterResource(R.drawable.zill),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(32.dp))

            // Title
            Text(
                text = state.currentTrack?.title ?: "Unknown Title",
                color = IcyPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // Artist
            Text(
                text = state.currentTrack?.artist ?: "Unknown Artist",
                color = IcySecondary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(28.dp))

            // Seek bar (assumed simple)
            MusicSeekBar(
                progressMs = state.progressMs,
                durationMs = state.durationMs,
                onSeek = onSeek,
            )

            Spacer(Modifier.height(32.dp))

            // Controls
            PlaybackControls(
                isPlaying = state.isPlaying,
                onPlayPause = onPlayPause,
                onPrevious = onPrevious,
                onNext = onNext,
            )

        }
    }
}


fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


@Preview(showBackground = true, backgroundColor = 0xFF080A12, widthDp = 390, heightDp = 844)
@Composable
private fun MusicPlayerScreenPreview() {
    MusicPlayerScreen(
        state = PlayerUiState(
            currentTrack = sampleTrack,
            isPlaying = true,
            progressMs = 62_000L,
        ),
        onPlayPause = {},
        onPrevious = {},
        onNext = {},
        onSeek = {},
    )
}


