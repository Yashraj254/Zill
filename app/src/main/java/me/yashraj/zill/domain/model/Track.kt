package me.yashraj.zill.domain.model

import android.net.Uri

data class Track(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String="Unknown Artist",
    val album: String,
    val albumId: Long,
    val artworkUri: Uri,
    val duration: Long, // milliseconds
    val path: String,
    val dateAdded: Long, // timestamp
    val dateModified: Long, // timestamp
    val size: Long, // bytes
    val mimeType: String,
    val trackNumber: Int? = null,
    val year: Int? = null
) {

    fun getFormattedDuration(): String {
        val seconds = (duration / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
        } else {
            String.format("%d:%02d", minutes, seconds % 60)
        }
    }

    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}