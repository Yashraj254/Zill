package me.yashraj.zill.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.yashraj.zill.ui.player.LoopMode
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playerDataStore: DataStore<Preferences> by preferencesDataStore(name = "player_prefs")

@Singleton
class PlayerPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        private val LOOP_MODE_KEY = stringPreferencesKey("loop_mode")
    }

    val loopMode: Flow<LoopMode> = context.playerDataStore.data.map { prefs ->
        LoopMode.valueOf(prefs[LOOP_MODE_KEY] ?: LoopMode.OFF.name)
    }

    suspend fun setLoopMode(mode: LoopMode) {
        context.playerDataStore.edit { prefs ->
            prefs[LOOP_MODE_KEY] = mode.name
        }
    }
}
