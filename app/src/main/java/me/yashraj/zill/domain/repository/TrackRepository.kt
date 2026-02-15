package me.yashraj.zill.domain.repository

import kotlinx.coroutines.flow.Flow
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.model.Track

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getTrackFolderPath(): Flow<List<Folder>>
    suspend fun getTrackById(id: Long?): Track?
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getTracksByArtist(artistId: Long): List<Track>
    suspend fun getTracksByAlbum(albumId: Long): List<Track>
    suspend fun getFolderTracks(path: String): Flow<List<Track>>
}
