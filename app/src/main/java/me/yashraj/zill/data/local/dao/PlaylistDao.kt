package me.yashraj.zill.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.yashraj.zill.data.local.entity.PlaylistEntity
import me.yashraj.zill.data.local.entity.PlaylistTrackEntity

@Dao
interface PlaylistDao {

    @Query("""
        SELECT p.id, p.name, p.created_at, p.is_default FROM playlist p
        LEFT JOIN playlist_track pt ON p.id = pt.playlist_id
        GROUP BY p.id
        ORDER BY p.is_default DESC, p.created_at DESC
    """)
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Upsert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlist WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("DELETE FROM playlist_track WHERE playlist_id = :playlistId")
    suspend fun deletePlaylistTracks(playlistId: Long)

    @Query("SELECT track_id FROM playlist_track WHERE playlist_id = :playlistId ORDER BY position ASC")
    fun getTrackIdsForPlaylist(playlistId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("SELECT MAX(position) FROM playlist_track WHERE playlist_id = :playlistId")
    suspend fun getMaxPosition(playlistId: Long): Int?

    @Query("SELECT COUNT(*) FROM playlist_track WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Int

    @Query("SELECT COUNT(*) FROM playlist_track WHERE playlist_id = :playlistId")
    fun getTrackCount(playlistId: Long): Flow<Int>
}
