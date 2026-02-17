package me.yashraj.zill.ui.player

import me.yashraj.zill.domain.model.Track

data class PlayerUiState(
    val currentTrack: Track? = null,
    val playlist: List<Track> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val isConnected: Boolean = false,
)