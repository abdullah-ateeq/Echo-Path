package com.example.echopath.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.echopath.model.GameSettings
import com.example.echopath.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "echo_path_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val BEST_ROUND = intPreferencesKey("best_round")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val PARTICLES_ENABLED = booleanPreferencesKey("particles_enabled")
        val GHOST_TRAILS_ENABLED = booleanPreferencesKey("ghost_trails_enabled")
        val REDUCED_EFFECTS = booleanPreferencesKey("reduced_effects")
        val TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
    }

    val bestRoundFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.BEST_ROUND] ?: 1
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        val themeModeStr = prefs[Keys.THEME_MODE] ?: ThemeMode.DARK.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.DARK)

        GameSettings(
            themeMode = themeMode,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            particlesEnabled = prefs[Keys.PARTICLES_ENABLED] ?: true,
            ghostTrailsEnabled = prefs[Keys.GHOST_TRAILS_ENABLED] ?: true,
            reducedEffects = prefs[Keys.REDUCED_EFFECTS] ?: false,
            tutorialCompleted = prefs[Keys.TUTORIAL_COMPLETED] ?: false
        )
    }

    suspend fun updateBestRound(round: Int) {
        context.dataStore.edit { prefs ->
            val currentBest = prefs[Keys.BEST_ROUND] ?: 1
            if (round > currentBest) {
                prefs[Keys.BEST_ROUND] = round
            }
        }
    }

    suspend fun resetBestRound() {
        context.dataStore.edit { prefs ->
            prefs[Keys.BEST_ROUND] = 1
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setParticlesEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PARTICLES_ENABLED] = enabled
        }
    }

    suspend fun setGhostTrailsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GHOST_TRAILS_ENABLED] = enabled
        }
    }

    suspend fun setReducedEffects(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REDUCED_EFFECTS] = enabled
        }
    }

    suspend fun setTutorialCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TUTORIAL_COMPLETED] = completed
        }
    }
}
