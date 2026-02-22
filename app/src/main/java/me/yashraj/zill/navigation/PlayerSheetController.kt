package me.yashraj.zill.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Two stable positions the sheet can rest in.
 *  MINI       → compact 72dp bar at the bottom
 *  EXPANDED   → full-screen player
 */
enum class PlayerSheetState { MINI, EXPANDED }

@Stable
class PlayerSheetController {
    var isVisible by mutableStateOf(false)
        private set

    fun show() {
        isVisible = true
    }

}

val LocalPlayerSheetController = staticCompositionLocalOf<PlayerSheetController> {
    error("No PlayerSheetController provided")
}
