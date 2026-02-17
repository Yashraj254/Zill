package me.yashraj.zill.ui.music

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.yashraj.zill.R
import me.yashraj.zill.domain.model.Track

@Composable
fun MusicTrackItem(
    track: Track,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        val horizontalPadding = if (maxWidth < 600.dp) 12.dp else 20.dp
        val imageSize = (maxWidth * 0.12f).coerceIn(48.dp, 72.dp)

        Row(
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.artworkUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.zill_logo),
                error = painterResource(R.drawable.zill_logo),
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(8.dp))
            )


            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = track.artist + " ● " + track.album,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = track.getFormattedDuration(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    name = "Phone",
    showBackground = true,
    widthDp = 360
)
//@PreviewScreenSizes
@Composable
private fun TrackItemPreview() {
    MaterialTheme {
        MusicTrackItem(
            track = previewTrack,
            onClick = {}
        )
    }
}

private val previewTrack = Track(
    id = 1L,
    uri = Uri.EMPTY,
    title = "Time",
    artist = "Pink Floyd",
    artistId = 1L,
    album = "The Dark Side of the Moon",
    albumId = 1L,
    artworkUri = Uri.EMPTY,
    duration = 412000,
    path = "",
    dateAdded = 0L,
    dateModified = 0L,
    size = 5_000_000,
    mimeType = "audio/mpeg"
)


