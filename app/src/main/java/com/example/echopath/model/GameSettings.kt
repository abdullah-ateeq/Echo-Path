package com.example.echopath.model

data class GameSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val particlesEnabled: Boolean = true,
    val ghostTrailsEnabled: Boolean = true,
    val reducedEffects: Boolean = false,
    val tutorialCompleted: Boolean = false
)

data class RunStats(
    val roundSurvived: Int = 0,
    val survivalTimeMs: Long = 0L,
    val echoesCreated: Int = 0,
    val closeCalls: Int = 0,
    val isNewBest: Boolean = false,
    val bestRound: Int = 0
) {
    val formattedSurvivalTime: String
        get() {
            val totalSeconds = (survivalTimeMs / 1000L).coerceAtLeast(0L)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
