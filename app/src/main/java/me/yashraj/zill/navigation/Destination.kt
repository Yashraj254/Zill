package me.yashraj.zill.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person4
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.yashraj.zill.R

enum class BottomDestination(
    val key: Screen,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    MUSIC(Screen.Music, Icons.Default.MusicNote, R.string.destination_music),
    FOLDERS(Screen.Folders, Icons.Default.Folder, R.string.destination_folders),
    ARTISTS(Screen.Artists, Icons.Default.Person4, R.string.destination_artists),
    ALBUMS(Screen.Albums, Icons.Default.Album, R.string.destination_albums),
}

sealed interface Screen : NavKey {
    @Serializable
    data object Music : Screen
    @Serializable
    data object Folders : Screen
    @Serializable
    data object Artists : Screen
    @Serializable
    data object Albums : Screen
    @Serializable
    data class ArtistTracks(val artistName: String, val artistId: Long) : Screen
    @Serializable
    data class AlbumTracks(val albumName: String, val albumId: Long) : Screen
    @Serializable
    data class FolderTracks(val folderPath: String) : Screen
}

val LocalNavBackStack = staticCompositionLocalOf<MutableList<NavKey>> {
    error("No NavBackStack provided")
}