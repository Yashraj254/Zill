package me.yashraj.zill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.yashraj.zill.ui.folder.FolderScreen
import me.yashraj.zill.ui.folder.tracks.FolderTrackScreen
import me.yashraj.zill.ui.music.MusicScreen
import java.util.Map.entry

@Composable
fun ZillNavDisplay(
    modifier: Modifier = Modifier
) {
    val backStack = LocalNavBackStack.current

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // Recommended decorators for production — handles state saving & ViewModel scoping
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
//            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {

            entry<Screen.Music> {
                MusicScreen()
            }

            entry<Screen.Folders> {
                FolderScreen(
                    onFolderClick = { folderId ->
                        backStack.add(Screen.FolderTracks(folderId = folderId))
                    }
                )
            }

            entry<Screen.FolderTracks> { key ->
                FolderTrackScreen(folderId = key.folderId)
            }
        }
    )
}