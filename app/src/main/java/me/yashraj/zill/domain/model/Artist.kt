package me.yashraj.zill.domain.model

import android.net.Uri

data class Artist(
    val id: Long,              // MediaStore artist ID
    val name: String,
    val albumCount: Int,
    val trackCount: Int,
    val thumbnailUri: Uri?,    // artist image (not in MediaStore, external source)
)