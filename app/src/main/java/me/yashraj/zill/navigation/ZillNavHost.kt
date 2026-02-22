package me.yashraj.zill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.yashraj.zill.ui.album.AlbumScreen
import me.yashraj.zill.ui.album.AlbumTrackScreen
import me.yashraj.zill.ui.artist.ArtistScreen
import me.yashraj.zill.ui.artist.ArtistTrackScreen
import me.yashraj.zill.ui.folder.FolderScreen
import me.yashraj.zill.ui.folder.FolderTrackScreen
import me.yashraj.zill.ui.music.MusicScreen

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

                else -> { }
            }
        },
        // handles state saving & ViewModel scoping
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

            entry<Screen.Artists> {
                ArtistScreen(onArtistClick = { id, name ->
                    backStack.add(Screen.ArtistTracks(artistId = id, artistName = name))
                })
            }

            entry<Screen.ArtistTracks> { key ->
                ArtistTrackScreen(artistId = key.artistId, artistName = key.artistName)
            }

            entry<Screen.Albums> {
                AlbumScreen(onAlbumClick = { id, name ->
                    backStack.add(Screen.AlbumTracks(albumId = id, albumName = name))
                })
            }

            entry<Screen.AlbumTracks> { key ->
                AlbumTrackScreen(albumId = key.albumId, albumName = key.albumName)
            }
        }
    )
}