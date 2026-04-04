package me.yashraj.zill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
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
import me.yashraj.zill.ui.playlist.PlaylistDetailScreen
import me.yashraj.zill.ui.playlist.PlaylistScreen

@Composable
fun ZillNavDisplay(
    modifier: Modifier = Modifier,
    startDestination: Screen,
    currentKey: NavKey
) {
    val backStack = LocalNavBackStack.current
    val appBar = LocalAppBarController.current

    key(currentKey) {
        SideEffect {
            appBar.clearSearch()
            when (val k = currentKey) {
                is Screen.Music -> appBar.update { copy(title = "Music", showBack = false, showSearch = true) }
                is Screen.Folders -> appBar.update { copy(title = "Folders", showBack = false, showSearch = true) }
                is Screen.Artists -> appBar.update { copy(title = "Artists", showBack = false, showSearch = true) }
                is Screen.Albums -> appBar.update { copy(title = "Albums", showBack = false, showSearch = true) }
                is Screen.Playlists -> appBar.update { copy(title = "Playlists", showBack = false, showSearch = true) }
                is Screen.ArtistTracks -> appBar.update { copy(title = k.artistName, showBack = true, showSearch = true) }
                is Screen.AlbumTracks -> appBar.update { copy(title = k.albumName, showBack = true, showSearch = true) }
                is Screen.FolderTracks -> appBar.update { copy(title = k.folderPath.takeLastWhile { it != '/' }, showBack = true, showSearch = true) }
                is Screen.PlaylistTracks -> appBar.update { copy(title = k.playlistName, showBack = true, showSearch = true) }
            }
        }
    }

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
                ArtistTrackScreen(artistId = key.artistId)
            }

            entry<Screen.Albums> {
                AlbumScreen(onAlbumClick = { id, name ->
                    backStack.add(Screen.AlbumTracks(albumId = id, albumName = name))
                })
            }

            entry<Screen.AlbumTracks> { key ->
                AlbumTrackScreen(albumId = key.albumId)
            }

            entry<Screen.Playlists> {
                PlaylistScreen(onPlaylistClick = { id, name ->
                    backStack.add(Screen.PlaylistTracks(playlistId = id, playlistName = name))
                })
            }

            entry<Screen.PlaylistTracks> { key ->
                PlaylistDetailScreen(
                    playlistId = key.playlistId
                )
            }
        }
    )
}