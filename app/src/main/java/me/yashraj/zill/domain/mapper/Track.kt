package me.yashraj.zill.domain.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import me.yashraj.zill.domain.model.Track

fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id.toString())
    .setUri(uri)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setArtworkUri(albumArtUri)
            .build()
    )
    .build()