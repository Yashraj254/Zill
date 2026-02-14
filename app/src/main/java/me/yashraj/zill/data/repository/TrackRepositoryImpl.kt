package me.yashraj.zill.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.yashraj.zill.data.MediaSource
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import java.io.File

class TrackRepositoryImpl(private val mediaSource: MediaSource): TrackRepository {

    override fun getAllTracks(): Flow<List<Track>> {
       return mediaSource.observeAudioFiles()
    }

    override fun getTrackFolderPath(): Flow<List<Folder>> {
        return getAllTracks()
            .map { tracks ->
                tracks
                    .groupBy { File(it.path).parent ?: "Unknown" }
                    .map { (dirPath, tracksInDir) ->
                        Folder(
                            path = dirPath,
                            name = File(dirPath).name,
                            trackCount = tracksInDir.size,
                        )
                    }
                    .sortedBy { it.name }
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun getTrackById(id: Long): Track? {
        TODO("Not yet implemented")
    }

    override suspend fun searchTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksByArtist(artistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksByAlbum(albumId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getFolderTracks(path: String): Flow<List<Track>> {
        val normalizedFolder = File(path).absolutePath
        return getAllTracks()
            .map { tracks ->
                tracks.filter { track ->
                    File(track.path).parentFile?.absolutePath == normalizedFolder
                }
            }
            .flowOn(Dispatchers.IO)
    }
}