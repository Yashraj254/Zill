package me.yashraj.zill.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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

    companion object {
        val seedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "INSERT INTO playlist (name, created_at, is_default) VALUES ('Favorites', ${System.currentTimeMillis()}, 1)"
                )
            }
        }
    }
}
