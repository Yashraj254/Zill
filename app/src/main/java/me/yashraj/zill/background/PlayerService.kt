package me.yashraj.zill.background

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import me.yashraj.zill.MainActivity
import me.yashraj.zill.background.PlayerService.Companion.ACTION_STOP


class PlayerService : MediaSessionService() {

    // The audio player owned by this service — never shared directly
    // with the UI layer (the UI talks to it through MediaController instead).
    private lateinit var player: Player

    // The Media3 session that bridges ExoPlayer to the system media controls, notification,
    // and any bound MediaController clients.
    private lateinit var mediaSession: MediaSession

    /**
     * Builds [ExoPlayer] with music-appropriate audio attributes and constructs the
     * [MediaSession] that exposes it to the system and to [PlayerManager] via IPC.
     */
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)  // pause on headphone unplug
            .build()

        // Opens the app when the user taps the media notification.
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCustomLayout(listOf(closeCommandButton()))
            .setCallback(MediaSessionCallback())
            .build()
    }

    /**
     * Builds the close (✕) [CommandButton] shown in the media notification.
     * Tapping it sends [ACTION_STOP] which stop the player and the service.
     */
    @OptIn(UnstableApi::class)
    private fun closeCommandButton(): CommandButton {
        return CommandButton.Builder(CommandButton.ICON_UNDEFINED)
            .setDisplayName("Close")
            .setCustomIconResId(android.R.drawable.ic_menu_close_clear_cancel)
            .setSessionCommand(SessionCommand(ACTION_STOP, Bundle.EMPTY))
            .build()
    }

    /** Required by [MediaSessionService] — returns the single active session. */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession

    /**
     * Called when the user swipes the app away from the Recents list.
     * Requires `android:stopWithTask="false"` in the manifest to reach here.
     * We stop playback gracefully rather than letting the OS kill the service mid-track.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        player.stop()
        player.clearMediaItems()
        mediaSession.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Releases the session before the player — Media3 requires this ordering to avoid
     * sending callbacks to an already-released player instance.
     */
    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {

        /**
         * Handles custom commands not covered by the standard player command set.
         * Currently only [ACTION_STOP] is expected, which fully tears down the service
         * when the user taps the close button in the notification.
         */
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == ACTION_STOP) {
                // User tapped ✕ in the notification — tear everything down cleanly.
                player.stop()
                player.clearMediaItems()
                mediaSession.release()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        /**
         * Called when a controller (e.g. [PlayerManager], the system media UI) connects.
         * We extend the default session command set with [ACTION_STOP] so Media3 doesn't
         * reject it as an unknown command when the notification button sends it.
         */
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                .buildUpon()
                .add(SessionCommand(ACTION_STOP, Bundle.EMPTY))
                .build()

            return MediaSession.ConnectionResult.accept(
                sessionCommands,
                MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
            )
        }
    }

    companion object {
        const val ACTION_STOP = "me.yashraj.zill.ACTION_STOP"
    }
}