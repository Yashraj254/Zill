package me.yashraj.zill.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.yashraj.zill.data.local.dao.PlaylistDao
import me.yashraj.zill.data.local.entity.PlaylistEntity
import me.yashraj.zill.data.local.entity.PlaylistTrackEntity
import me.yashraj.zill.domain.model.Playlist
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.PlaylistRepository
import me.yashraj.zill.domain.repository.TrackRepository
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val trackRepository: TrackRepository
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map { list ->
            list.map { Playlist(it.id, it.name, it.trackCount, it.createdAt) }
        }

    override fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> =
        combine(
            playlistDao.getTrackIdsForPlaylist(playlistId),
            trackRepository.getAllTracks()
        ) { ids, allTracks ->
            val idSet = ids.toSet()
            allTracks
                .filter { it.id in idSet }
                .sortedBy { ids.indexOf(it.id) }
        }

    override suspend fun createPlaylist(name: String): Long =
        playlistDao.insertPlaylist(PlaylistEntity(name = name))

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistTracks(playlistId)
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val maxPos = playlistDao.getMaxPosition(playlistId) ?: -1
        playlistDao.addTrackToPlaylist(PlaylistTrackEntity(playlistId, trackId, maxPos + 1))
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun createPlaylistAndAddTrack(name: String, trackId: Long) {
        val playlistId = createPlaylist(name)
        addTrackToPlaylist(playlistId, trackId)
    }
}
