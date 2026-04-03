package me.yashraj.zill.domain.repository

import kotlinx.coroutines.flow.Flow
import me.yashraj.zill.domain.model.Playlist
import me.yashraj.zill.domain.model.Track

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun createPlaylistAndAddTrack(name: String, trackId: Long)
}
