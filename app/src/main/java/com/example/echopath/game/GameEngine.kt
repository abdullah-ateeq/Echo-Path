package com.example.echopath.game

import androidx.compose.ui.geometry.Offset
import com.example.echopath.model.EchoPath
import com.example.echopath.model.GameState
import com.example.echopath.model.GameStatus
import com.example.echopath.model.PathPoint
import com.example.echopath.model.RunStats
import com.example.echopath.ui.theme.getGhostColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class GameEngine(
    val soundManager: SoundManager,
    val hapticManager: HapticManager,
    val particleManager: ParticleManager
) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val random = Random(System.currentTimeMillis())

    // Recording sampling
    private var lastRecordedTimeMs = 0L
    private val sampleIntervalMs = 33L // ~30 samples/sec

    // Transition timers
    private var roundTransitionTimerSec = 0f
    private val roundTransitionDurationSec = 0.65f

    private var bannerTimerSec = 0f
    private val bannerDurationSec = 0.9f

    private var lastCloseCallCooldown = 0f

    // Current run stats
    var currentStats: RunStats = RunStats()
        private set

    private var isTutorialCompleted = false

    fun initNewGame(bestRound: Int, tutorialCompleted: Boolean) {
        particleManager.clear()
        isTutorialCompleted = tutorialCompleted
        lastRecordedTimeMs = 0L
        roundTransitionTimerSec = 0f
        bannerTimerSec = 0f
        lastCloseCallCooldown = 0f

        val initialPlayerPos = Offset(0.5f, 0.5f)
        val initialRecording = mutableListOf(PathPoint(0.5f, 0.5f, 0L))

        currentStats = RunStats(
            roundSurvived = 0,
            survivalTimeMs = 0L,
            echoesCreated = 0,
            closeCalls = 0,
            isNewBest = false,
            bestRound = bestRound
        )

        val tutorialHint = if (!tutorialCompleted) "DRAG TO MOVE" else null

        _gameState.value = GameState(
            roundNumber = 1,
            bestRound = bestRound,
            status = GameStatus.READY,
            playerPos = initialPlayerPos,
            targetPlayerPos = initialPlayerPos,
            roundElapsedMs = 0L,
            roundDurationMs = 5000L,
            currentRecording = initialRecording,
            echoes = emptyList(),
            activeGhostPositions = emptyList(),
            spawnGracePeriodActive = true,
            bannerText = "ROUND 1",
            bannerSubText = if (!tutorialCompleted) "DRAG TO MOVE" else null,
            bannerAlpha = 1f,
            bannerScale = 1f,
            tutorialHint = tutorialHint
        )

        soundManager.playRoundStart()
        hapticManager.vibrateRoundStart()
    }

    fun onDragDelta(deltaNormX: Float, deltaNormY: Float) {
        val state = _gameState.value
        if (state.status != GameStatus.PLAYING && state.status != GameStatus.READY) return

        if (state.status == GameStatus.READY) {
            _gameState.value = state.copy(status = GameStatus.PLAYING)
        }

        val padding = CollisionDetector.PLAYER_RADIUS
        val currentTarget = state.targetPlayerPos
        val newTargetX = (currentTarget.x + deltaNormX).coerceIn(padding, 1f - padding)
        val newTargetY = (currentTarget.y + deltaNormY).coerceIn(padding, 1f - padding)

        _gameState.value = state.copy(
            targetPlayerPos = Offset(newTargetX, newTargetY)
        )
    }

    fun pause() {
        val state = _gameState.value
        if (state.status == GameStatus.PLAYING || state.status == GameStatus.READY) {
            _gameState.value = state.copy(status = GameStatus.PAUSED)
            soundManager.playClick()
        }
    }

    fun resume() {
        val state = _gameState.value
        if (state.status == GameStatus.PAUSED) {
            _gameState.value = state.copy(status = GameStatus.PLAYING)
            soundManager.playClick()
        }
    }

    fun update(dtSec: Float) {
        val dt = dtSec.coerceIn(0.001f, 0.05f)
        val state = _gameState.value

        particleManager.update(dt)

        when (state.status) {
            GameStatus.READY -> {
                updateReadyState(dt)
            }
            GameStatus.PLAYING -> {
                updatePlayingState(dt)
            }
            GameStatus.ROUND_TRANSITION -> {
                updateRoundTransitionState(dt)
            }
            GameStatus.COLLAPSE -> {
                updateCollapseState(dt)
            }
            GameStatus.PAUSED, GameStatus.GAME_OVER -> {
                // Idle
            }
        }
    }

    private fun updateReadyState(dt: Float) {
        bannerTimerSec += dt
        val bannerProgress = (bannerTimerSec / bannerDurationSec).coerceIn(0f, 1f)
        val alpha = if (bannerProgress < 0.7f) 1f else 1f - (bannerProgress - 0.7f) / 0.3f

        if (bannerTimerSec >= bannerDurationSec) {
            _gameState.value = _gameState.value.copy(
                status = GameStatus.PLAYING,
                bannerText = null,
                bannerSubText = null
            )
        } else {
            _gameState.value = _gameState.value.copy(
                bannerAlpha = alpha,
                bannerScale = 1f + bannerProgress * 0.15f
            )
        }
    }

    private fun updatePlayingState(dt: Float) {
        val state = _gameState.value
        val round = state.roundNumber

        // Difficulty scaling (subtle replay speedup for later rounds)
        val speedMultiplier = when {
            round <= 5 -> 1.0f
            round <= 10 -> 1.05f
            else -> 1.10f
        }

        val addedElapsedMs = (dt * 1000f * speedMultiplier).toLong()
        val newElapsedMs = state.roundElapsedMs + addedElapsedMs

        // Smooth player position interpolation towards target
        val smoothingFactor = 22f
        val lerpFactor = 1f - exp(-smoothingFactor * dt)
        val newPlayerX = state.playerPos.x + (state.targetPlayerPos.x - state.playerPos.x) * lerpFactor
        val newPlayerY = state.playerPos.y + (state.targetPlayerPos.y - state.playerPos.y) * lerpFactor
        val newPlayerPos = Offset(newPlayerX, newPlayerY)

        // Spawn player trail particles
        particleManager.spawnPlayerTrail(newPlayerPos)

        // Record player position at fixed frequency
        val currentRecording = state.currentRecording.toMutableList()
        if (newElapsedMs - lastRecordedTimeMs >= sampleIntervalMs) {
            currentRecording.add(PathPoint(newPlayerX, newPlayerY, newElapsedMs))
            lastRecordedTimeMs = newElapsedMs
        }

        // Compute ghost positions
        val ghostPositions = state.echoes.map { echo ->
            val pos = echo.getPositionAt(newElapsedMs)
            particleManager.spawnGhostTrail(pos, getGhostColor(echo.generation))
            pos
        }

        // Spawn safety grace period (first 400ms)
        val isGracePeriod = newElapsedMs < 400L

        // Screen pulse decay
        val newScreenEdgeAlpha = (state.screenEdgePulseAlpha - dt * 3.5f).coerceAtLeast(0f)

        // Collision & Close Call detection
        var collisionDetected = false
        var collisionAt: Offset? = null
        var isCloseCallNow = false
        lastCloseCallCooldown -= dt

        if (!isGracePeriod) {
            for (ghostPos in ghostPositions) {
                if (CollisionDetector.checkCollision(newPlayerPos, ghostPos)) {
                    collisionDetected = true
                    collisionAt = ghostPos
                    break
                } else if (CollisionDetector.isCloseCall(newPlayerPos, ghostPos)) {
                    isCloseCallNow = true
                }
            }
        }

        if (collisionDetected && collisionAt != null) {
            handleCollision(collisionAt, newPlayerPos)
            return
        }

        // Handle close call event
        var closeCallsThisRound = state.closeCallCountThisRound
        var totalCloseCalls = state.totalCloseCalls
        var screenEdgeAlpha = newScreenEdgeAlpha

        if (isCloseCallNow && lastCloseCallCooldown <= 0f) {
            lastCloseCallCooldown = 0.35f
            closeCallsThisRound++
            totalCloseCalls++
            screenEdgeAlpha = 0.85f

            soundManager.playCloseCall()
            hapticManager.vibrateCloseCall()
            particleManager.spawnCloseCallSparks(newPlayerPos)
        }

        // Check for round completion
        if (newElapsedMs >= state.roundDurationMs) {
            handleRoundComplete(newPlayerPos, currentRecording, closeCallsThisRound, totalCloseCalls)
            return
        }

        // Update state
        _gameState.value = state.copy(
            playerPos = newPlayerPos,
            roundElapsedMs = newElapsedMs,
            currentRecording = currentRecording,
            activeGhostPositions = ghostPositions,
            spawnGracePeriodActive = isGracePeriod,
            closeCallActive = isCloseCallNow,
            closeCallCountThisRound = closeCallsThisRound,
            totalCloseCalls = totalCloseCalls,
            totalSurvivalTimeMs = state.totalSurvivalTimeMs + addedElapsedMs,
            screenEdgePulseAlpha = screenEdgeAlpha
        )
    }

    private fun handleRoundComplete(
        lastPlayerPos: Offset,
        recording: List<PathPoint>,
        closeCallsThisRound: Int,
        totalCloseCalls: Int
    ) {
        val state = _gameState.value
        val round = state.roundNumber
        val isClean = closeCallsThisRound == 0

        // Create new EchoPath
        val newEcho = EchoPath(
            generation = round,
            points = recording,
            durationMs = state.roundDurationMs
        )

        // Cap echoes at 12 to preserve performance and clarity
        val updatedEchoes = (state.echoes + newEcho).takeLast(12)

        val echoColor = getGhostColor(round)
        particleManager.spawnEchoCreatedPulse(lastPlayerPos, echoColor)

        if (isClean) {
            soundManager.playCleanLoop()
        } else {
            soundManager.playEchoCreated()
        }
        hapticManager.vibrateEchoCreated()

        roundTransitionTimerSec = 0f

        val banner = if (isClean) "CLEAN LOOP" else "ECHO CREATED"
        val subBanner = if (!isTutorialCompleted && round == 1) "YOUR PATH WILL RETURN" else null

        _gameState.value = state.copy(
            status = GameStatus.ROUND_TRANSITION,
            echoes = updatedEchoes,
            bannerText = banner,
            bannerSubText = subBanner,
            bannerAlpha = 1f,
            bannerScale = 1.15f,
            isCleanLoopThisRound = isClean,
            closeCallCountThisRound = 0,
            totalCloseCalls = totalCloseCalls,
            tutorialHint = if (!isTutorialCompleted && round == 1) "AVOID YOUR ECHO" else null
        )
    }

    private fun updateRoundTransitionState(dt: Float) {
        roundTransitionTimerSec += dt
        val state = _gameState.value

        // Decay screen pulse
        val newScreenEdgeAlpha = (state.screenEdgePulseAlpha - dt * 2.5f).coerceAtLeast(0f)

        if (roundTransitionTimerSec >= roundTransitionDurationSec) {
            // Start next round!
            val nextRound = state.roundNumber + 1
            val center = Offset(0.5f, 0.5f)

            lastRecordedTimeMs = 0L
            bannerTimerSec = 0f

            val subText = if (!isTutorialCompleted && nextRound == 2) "AVOID YOUR ECHO" else null

            _gameState.value = state.copy(
                roundNumber = nextRound,
                status = GameStatus.READY,
                playerPos = center,
                targetPlayerPos = center,
                roundElapsedMs = 0L,
                currentRecording = mutableListOf(PathPoint(0.5f, 0.5f, 0L)),
                activeGhostPositions = state.echoes.map { it.getPositionAt(0L) },
                spawnGracePeriodActive = true,
                bannerText = "ROUND $nextRound",
                bannerSubText = subText,
                bannerAlpha = 1f,
                bannerScale = 1f,
                screenEdgePulseAlpha = newScreenEdgeAlpha,
                isCleanLoopThisRound = false,
                closeCallCountThisRound = 0
            )

            soundManager.playRoundStart()
            hapticManager.vibrateRoundStart()
        } else {
            val progress = (roundTransitionTimerSec / roundTransitionDurationSec).coerceIn(0f, 1f)
            _gameState.value = state.copy(
                bannerAlpha = (1f - progress * 0.4f).coerceIn(0f, 1f),
                screenEdgePulseAlpha = newScreenEdgeAlpha
            )
        }
    }

    private fun handleCollision(collisionPoint: Offset, playerPoint: Offset) {
        val state = _gameState.value

        soundManager.playCollision()
        hapticManager.vibrateCollision()
        particleManager.spawnCollisionBurst(playerPoint)

        val survivedRounds = state.roundNumber - 1
        val isNewBest = survivedRounds > state.bestRound
        val bestScore = if (isNewBest) survivedRounds else state.bestRound

        currentStats = RunStats(
            roundSurvived = survivedRounds,
            survivalTimeMs = state.totalSurvivalTimeMs,
            echoesCreated = state.echoes.size,
            closeCalls = state.totalCloseCalls,
            isNewBest = isNewBest,
            bestRound = bestScore
        )

        _gameState.value = state.copy(
            status = GameStatus.COLLAPSE,
            collisionPoint = collisionPoint,
            collapseProgress = 0f,
            screenShakeOffset = Offset(random.nextFloat() * 18f - 9f, random.nextFloat() * 18f - 9f)
        )
    }

    private fun updateCollapseState(dt: Float) {
        val state = _gameState.value
        val progress = (state.collapseProgress + dt / 0.75f).coerceIn(0f, 1f)

        // Screen shake decay
        val shakeMag = (1f - progress) * 16f
        val shakeOffset = if (progress < 0.7f) {
            Offset(
                (random.nextFloat() * 2f - 1f) * shakeMag,
                (random.nextFloat() * 2f - 1f) * shakeMag
            )
        } else {
            Offset.Zero
        }

        if (progress >= 1f) {
            _gameState.value = state.copy(
                status = GameStatus.GAME_OVER,
                collapseProgress = 1f,
                screenShakeOffset = Offset.Zero
            )
        } else {
            _gameState.value = state.copy(
                collapseProgress = progress,
                screenShakeOffset = shakeOffset
            )
        }
    }
}
