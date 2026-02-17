package me.yashraj.zill.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

// ─────────────────────────────────────────────────────────────────────────────
// DRAG ANCHORS
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Two stable positions the sheet can rest in.
 *  MINI       → compact 72dp bar at the bottom
 *  EXPANDED   → full-screen player
 */
enum class PlayerSheetState { MINI, EXPANDED }

// ─────────────────────────────────────────────────────────────────────────────
// PLAYER STATE HOLDER  (hoist this in your ViewModel in production)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Lightweight holder for "is the player visible at all?"
 * Combine with your real PlayerState / ViewModel as needed.
 */
@Stable
class PlayerSheetController {
    var isVisible by mutableStateOf(false)
        private set

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

val LocalPlayerSheetController = staticCompositionLocalOf<PlayerSheetController> {
    error("No PlayerSheetController provided")
}
