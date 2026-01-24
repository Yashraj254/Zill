package me.yashraj.zill.data.repository

import kotlinx.coroutines.flow.Flow
import me.yashraj.zill.data.MediaSource
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository

class TrackRepositoryImpl(private val mediaSource: MediaSource): TrackRepository {

    override fun getAllTracks(): Flow<List<Track>> {
       return mediaSource.observeAudioFiles()
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
}