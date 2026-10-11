package com.example.echopath.model

import androidx.compose.ui.geometry.Offset

data class GameState(
    val roundNumber: Int = 1,
    val bestRound: Int = 1,
    val status: GameStatus = GameStatus.READY,
    val playerPos: Offset = Offset(0.5f, 0.5f),
    val targetPlayerPos: Offset = Offset(0.5f, 0.5f),
    val roundElapsedMs: Long = 0L,
    val roundDurationMs: Long = 5000L,
    val currentRecording: List<PathPoint> = emptyList(),
    val echoes: List<EchoPath> = emptyList(),
    val activeGhostPositions: List<Offset> = emptyList(),
    val spawnGracePeriodActive: Boolean = true,
    val closeCallActive: Boolean = false,
    val closeCallCountThisRound: Int = 0,
    val totalCloseCalls: Int = 0,
    val totalSurvivalTimeMs: Long = 0L,
    val bannerText: String? = "ROUND 1",
    val bannerSubText: String? = null,
    val bannerAlpha: Float = 1f,
    val bannerScale: Float = 1f,
    val collisionPoint: Offset? = null,
    val collapseProgress: Float = 0f,
    val screenShakeOffset: Offset = Offset.Zero,
    val screenEdgePulseAlpha: Float = 0f,
    val isCleanLoopThisRound: Boolean = false,
    val tutorialHint: String? = null
) {
    val roundProgress: Float
        get() = (roundElapsedMs.toFloat() / roundDurationMs.toFloat()).coerceIn(0f, 1f)

    val isPlaying: Boolean
        get() = status == GameStatus.PLAYING

    val echoesCreatedCount: Int
        get() = echoes.size
}
