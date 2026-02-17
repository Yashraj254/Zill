package me.yashraj.zill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.yashraj.zill.ui.folder.FolderScreen
import me.yashraj.zill.ui.folder.tracks.FolderTrackScreen
import me.yashraj.zill.ui.music.MusicScreen
import java.util.Map.entry

@Composable
fun ZillNavDisplay(
    modifier: Modifier = Modifier,
    startDestination: Screen,
    currentKey: NavKey
) {
    val backStack = LocalNavBackStack.current

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            when {
                backStack.size > 1 -> {
                    backStack.removeLastOrNull()
                }

                currentKey::class != startDestination::class -> {
                    backStack.clear()
                    backStack.add(startDestination)
                }

                else -> {
                    // Let system handle app exit
                }
            }
        },
        // Recommended decorators for production — handles state saving & ViewModel scoping
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<Screen.Music> {
                MusicScreen()
            }

            entry<Screen.Folders> {
                FolderScreen(
                    onFolderClick = { folderPath ->
                        backStack.add(Screen.FolderTracks(folderPath = folderPath))
                    }
                )
            }

            entry<Screen.FolderTracks> { key ->
                FolderTrackScreen(folderPath = key.folderPath)
            }
        }
    )
}