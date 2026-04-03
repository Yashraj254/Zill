package me.yashraj.zill.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.yashraj.zill.data.local.dao.PlaylistDao
import me.yashraj.zill.data.local.entity.PlaylistEntity
import me.yashraj.zill.data.local.entity.PlaylistTrackEntity

@Database(
    entities = [PlaylistEntity::class, PlaylistTrackEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ZillDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
