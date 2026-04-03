package me.yashraj.zill.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "playlist_track",
    primaryKeys = ["playlist_id", "track_id"]
)
data class PlaylistTrackEntity(
    @ColumnInfo(name = "playlist_id") val playlistId: Long,
    @ColumnInfo(name = "track_id") val trackId: Long,
    @ColumnInfo(name = "position") val position: Int
)
