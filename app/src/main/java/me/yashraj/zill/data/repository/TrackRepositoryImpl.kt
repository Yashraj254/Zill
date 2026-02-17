package me.yashraj.zill.data.repository

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.yashraj.zill.data.MediaSource
import me.yashraj.zill.domain.model.Album
import me.yashraj.zill.domain.model.Artist
import me.yashraj.zill.domain.model.Folder
import me.yashraj.zill.domain.model.Track
import me.yashraj.zill.domain.repository.TrackRepository
import java.io.File

class TrackRepositoryImpl(private val mediaSource: MediaSource) : TrackRepository {

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

    override suspend fun getTrackById(id: Long?): Track? {
        return getAllTracks()
            .map { tracks -> tracks.firstOrNull { it.id == id } }
            .flowOn(Dispatchers.IO)
            .firstOrNull()
    }

    override suspend fun searchTracks(query: String): List<Track> {
        TODO("Not yet implemented")
    }

    override fun getArtists(): Flow<List<Artist>> {
        return getAllTracks()
            .map { tracks ->
                tracks
                    .groupBy { it.artistId }
                    .map { (artistId, artistTracks) ->
                        val first = artistTracks.first()
                        Artist(
                            id = artistId,
                            name = first.artist,
                            albumCount = artistTracks.distinctBy { it.albumId }.size,
                            trackCount = artistTracks.size,
                            thumbnailUri = first.artworkUri
                        )
                    }
                    .sortedBy { it.name }
            }.flowOn(Dispatchers.IO)
    }

    override fun getTracksByArtist(artistId: Long): Flow<List<Track>> {
        return getAllTracks().map { tracks ->
            tracks.filter { it.artistId == artistId }
        }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return getAllTracks()
            .map { tracks ->
                tracks
                    .groupBy { it.albumId }
                    .map { (albumId, albumTracks) ->
                        val first = albumTracks.first()
                        Album(
                            id = albumId,
                            title = first.album,
                            artistId = first.artistId,
                            artistName = first.artist,
                            trackCount = albumTracks.size,
                            year = first.year,
                            albumArtUri = first.artworkUri
                        )
                    }
                    .sortedBy { it.title }
            }.flowOn(Dispatchers.IO)
    }

    override fun getTracksByAlbum(albumId: Long): Flow<List<Track>> {
        return getAllTracks().map { tracks ->
            tracks.filter { it.albumId == albumId }
        }
    }

    override fun getFolderTracks(path: String): Flow<List<Track>> {
        val normalizedFolder = File(path).absolutePath
        return getAllTracks()
            .map { tracks ->
                tracks.filter { track ->
                    File(track.path).parentFile?.absolutePath == normalizedFolder
                }
            }
            .flowOn(Dispatchers.IO)
    }


    override suspend fun getTrackByUri(uri: Uri): Track? {
        // Case 1 – URI is already a MediaStore URI like:
        //   content://media/external/audio/media/42
        // Pull the numeric ID from the last path segment and look it up directly.
        uri.lastPathSegment?.toLongOrNull()?.let { id ->
            val track = getTrackById(id)
            if (track != null) return track
        }

        // Case 2 – URI is a file:// or an opaque content:// from a third-party
        // file manager. Resolve to a real path, then match against known tracks.
        val resolvedPath: String? = when (uri.scheme) {
            "file" -> uri.path
            "content" -> mediaSource.resolvePathFromUri(uri)
            else -> null
        }

        if (resolvedPath != null) {
            return getAllTracks()
                .map { tracks -> tracks.firstOrNull { it.path == resolvedPath } }
                .flowOn(Dispatchers.IO)
                .firstOrNull()
        }

        return null
    }
}