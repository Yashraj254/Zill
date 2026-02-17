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
                        Uri.parse("content://media/external/audio/albumart"),
                        c.getLong(albumIdColumn)
                    )

                    val track = Track(
                        id = id,
                        uri = contentUri,
                        title = c.getString(titleColumn) ?: "Unknown Title",
                        artist = c.getString(artistColumn) ?: "Unknown Artist",
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
}