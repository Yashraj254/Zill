package me.yashraj.zill

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.yashraj.zill.navigation.ZillApp
import me.yashraj.zill.ui.player.PlayerViewModel
import me.yashraj.zill.ui.theme.ZillTheme
import me.yashraj.zill.utils.EXTRA_OPEN_PLAYER_EXPANDED
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZillTheme {
                ZillApp()
            }
        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.d("Received intent: %s", intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Timber.d("Handling intent: %s", intent)

        when (intent.action) {
            Intent.ACTION_VIEW -> {
                // Deep link / "Open with" audio file
                intent.data?.let { uri ->
                    Timber.d("Play from uri: %s", uri)
                    viewModel.playFromUri(uri)
                }
            }
        }

        // Notification-triggered expansion
        if (intent.getBooleanExtra(EXTRA_OPEN_PLAYER_EXPANDED, false)) {
            Timber.d("Requesting player expand from notification")
            viewModel.requestOpenExpanded()
        }
    }

}

