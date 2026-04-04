package me.yashraj.zill.ui.player

import androidx.media3.common.Player

enum class LoopMode {
    OFF, ALL, ONE;

    fun next(): LoopMode = when (this) {
        OFF -> ALL
        ALL -> ONE
        ONE -> OFF
    }

    fun toMedia3RepeatMode(): Int = when (this) {
        OFF -> Player.REPEAT_MODE_OFF
        ALL -> Player.REPEAT_MODE_ALL
        ONE -> Player.REPEAT_MODE_ONE
    }
}