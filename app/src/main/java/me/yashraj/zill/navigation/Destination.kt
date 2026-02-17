package me.yashraj.zill.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
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
}

sealed interface Screen : NavKey {
    @Serializable data object Music : Screen
    @Serializable data object Folders : Screen
    @Serializable data class FolderTracks(val folderPath: String) : Screen
}

val LocalNavBackStack = staticCompositionLocalOf<MutableList<NavKey>> {
    error("No NavBackStack provided")
}