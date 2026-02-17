package me.yashraj.zill.data

import me.yashraj.zill.domain.model.Track

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Singleton
class MediaSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    companion object {

        private val AUDIO_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
        )

        // Selection criteria - only music files, no notifications/ringtones
        private const val SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        // Sort order - alphabetically by title
        private const val SORT_ORDER = "${MediaStore.Audio.Media.TITLE} ASC"
    }

    /**
     * Observes audio files with live updates when media store changes.
     * Emits a new list whenever files are added, removed, or modified.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAudioFiles(): Flow<List<Track>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                // Trigger new query when media store changes
                trySend(Unit)
            }
        }

        // Register observer for external audio content
        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

       // Emit initial data
        trySend(Unit)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.mapLatest {
        getAllAudioFiles()
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches all audio files from device storage (one-time query).
     */
    private suspend fun getAllAudioFiles(): List<Track> = withContext(Dispatchers.IO) {
        val audioList = mutableListOf<Track>()

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val cursor: Cursor? = contentResolver.query(
            uri,
            AUDIO_PROJECTION,
            SELECTION,
            null,
            SORT_ORDER
        )

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val mimeTypeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val trackColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

            while (c.moveToNext()) {
                try {
                    val id = c.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val albumArtUri = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        c.getLong(albumIdColumn)
                    )

                    val track = Track(
                        id = id,
                        uri = contentUri,
                        title = c.getString(titleColumn) ?: "Unknown Title",
                        artist = c.getString(artistColumn) ?: "Unknown Artist",
                        artistId = c.getLong(artistIdColumn),
                        album = c.getString(albumColumn) ?: "Unknown Album",
                        albumId = c.getLong(albumIdColumn),
                        artworkUri = albumArtUri,
                        duration = c.getLong(durationColumn),
                        path = c.getString(dataColumn) ?: "",
                        dateAdded = c.getLong(dateAddedColumn),
                        dateModified = c.getLong(dateModifiedColumn),
                        size = c.getLong(sizeColumn),
                        mimeType = c.getString(mimeTypeColumn) ?: "audio/*",
                        trackNumber = c.getIntOrNull(trackColumn),
                        year = c.getIntOrNull(yearColumn)
                    )

                    audioList.add(track)
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing audio track at position ${c.position}")
                }
            }
        }

        Timber.d("Loaded ${audioList.size} audio files")
        audioList
    }

    private fun Cursor.getIntOrNull(columnIndex: Int): Int? {
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }

    /**
     * Resolves any content:// or file:// URI to an absolute filesystem path.
     *
     * Handles three URI types in order:
     *  1. DocumentsProvider URIs from the system file picker / Files app
     *     (e.g. content://com.android.externalstorage.documents/document/primary:Music/song.mp3)
     *  2. Generic content:// URIs — queries MediaStore.MediaColumns.DATA
     *  3. file:// URIs — path is already embedded in the URI
     */
    suspend fun resolvePathFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        // 1. DocumentsProvider URI (Files app, Storage Access Framework)
        //    These encode the real path as "primary:relative/path" or
        //    "<volumeId>:relative/path" inside the last path segment.
        decodeDocumentUri(uri)?.let { return@withContext it }

        // 2. Generic content:// — try reading the DATA column directly
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        try {
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val col = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    if (col >= 0) {
                        val path = cursor.getString(col)
                        if (!path.isNullOrBlank()) return@withContext path
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "resolvePathFromUri: DATA column query failed for $uri")
        }

        // 3. file:// — path is already there
        uri.path?.takeIf { it.startsWith("/") }
    }

    /**
     * Decodes a DocumentsProvider URI into a real filesystem path.
     *
     * The document ID embedded in these URIs looks like:
     *   "primary:New folder/song.mp3"   →  /storage/emulated/0/New folder/song.mp3
     *   "1A2B-3C4D:Music/track.flac"   →  /storage/1A2B-3C4D/Music/track.flac
     *
     * Returns null for any URI that doesn't follow this format.
     */
    private fun decodeDocumentUri(uri: Uri): String? {
        // Only applies to content:// URIs from the external storage documents provider
        // or the generic documents UI. Pattern: …/document/<docId>
        val pathSegments = uri.pathSegments
        val docSegmentIndex = pathSegments.indexOf("document")
        if (docSegmentIndex < 0 || docSegmentIndex + 1 >= pathSegments.size) return null

        // The document ID is everything after "/document/" — it may itself contain "/"
        // so we reconstruct it from the raw path rather than just pathSegments[docSegmentIndex+1]
        val rawPath = uri.path ?: return null
        val prefix = "/document/"
        val docId = rawPath.substringAfter(prefix, missingDelimiterValue = "")
            .let { Uri.decode(it) }   // decode %3A → : and %2F → /

        if (!docId.contains(":")) return null

        val (volume, relativePath) = docId.split(":", limit = 2)

        return when {
            volume.equals("primary", ignoreCase = true) ->
                "/storage/emulated/0/$relativePath"
            volume.matches(Regex("[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}")) ->
                // SD card or USB volume with an ID like "1A2B-3C4D"
                "/storage/$volume/$relativePath"
            else -> null
        }
    }
}