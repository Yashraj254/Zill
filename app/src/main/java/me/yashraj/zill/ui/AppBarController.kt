package me.yashraj.zill.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
class AppBarController {
    var state by mutableStateOf(
        AppBarState(title = "Zill", showBack = false)
    )

    fun update(block: AppBarState.() -> AppBarState) {
        state = state.block()
    }
}

data class AppBarState(
    val title: String,
    val showBack: Boolean
)


val LocalAppBarController =
    staticCompositionLocalOf<AppBarController> {
        error("No AppBarController")
    }