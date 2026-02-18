package me.yashraj.zill.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

data class AppBarState(
    val title: String = "",
    val showBack: Boolean = false,
    val showSearch: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
)

@Stable
class AppBarController {
    var state by mutableStateOf(AppBarState())
        private set

    fun update(block: AppBarState.() -> AppBarState) {
        state = state.block()
    }

    fun onSearchQueryChange(query: String) {
        state = state.copy(searchQuery = query)
    }

    fun toggleSearch() {
        state = state.copy(
            isSearchActive = !state.isSearchActive,
            searchQuery = if (state.isSearchActive) "" else state.searchQuery
        )
    }

    fun clearSearch() {
        state = state.copy(isSearchActive = false, searchQuery = "")
    }
}
val LocalAppBarController =
    staticCompositionLocalOf<AppBarController> {
        error("No AppBarController")
    }