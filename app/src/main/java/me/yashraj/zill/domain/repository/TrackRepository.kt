package me.yashraj.zill.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import me.yashraj.zill.domain.model.Album
import me.yashraj.zill.domain.model.Artist
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.model.Track

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getTrackFolderPath(): Flow<List<Folder>>
    suspend fun getTrackById(id: Long?): Track?
    suspend fun searchTracks(query: String): List<Track>
    fun getArtists(): Flow<List<Artist>>
    fun getTracksByArtist(artistId: Long): Flow<List<Track>>
    fun getAlbums(): Flow<List<Album>>
    fun getTracksByAlbum(albumId: Long): Flow<List<Track>>
    fun getFolderTracks(path: String): Flow<List<Track>>

    /**
     * Resolves a content:// or file:// URI to a Track already indexed in
     * MediaStore.  Returns null if the URI cannot be matched.
     */
    suspend fun getTrackByUri(uri: Uri): Track?
}
