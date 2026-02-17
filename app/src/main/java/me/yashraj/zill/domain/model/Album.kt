package me.yashraj.zill.domain.model

import android.net.Uri

data class Album(
    val id: Long,              // MediaStore album ID
    val title: String,
    val artistId: Long,
    val artistName: String,
    val trackCount: Int,
    val year: Int?,
    val albumArtUri: Uri?,     // MediaStore.Audio.Albums.ALBUM_ART or embedded
)