package com.example.echopath.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.echopath.game.CollisionDetector
import com.example.echopath.game.GameEngine
import com.example.echopath.model.GameState
import com.example.echopath.model.GameStatus
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.PlayerCore
import com.example.echopath.ui.theme.PlayerCyan
import com.example.echopath.ui.theme.PlayerCyanGlow
import com.example.echopath.ui.theme.PlayerHalo
import com.example.echopath.ui.theme.getGhostColor
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ArenaCanvas(
    gameEngine: GameEngine,
    gameState: GameState,
    ghostTrailsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalEchoPathColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "arenaPulse")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val totalWidth = constraints.maxWidth.toFloat()
        val totalHeight = constraints.maxHeight.toFloat()

        // Calculate contained arena dimensions (preserving a balanced rounded rectangle shape)
        val horizontalMargin = 32f
        val verticalMargin = 32f
        val availWidth = (totalWidth - horizontalMargin * 2f).coerceAtLeast(100f)
        val availHeight = (totalHeight - verticalMargin * 2f).coerceAtLeast(100f)

        // Aspect ratio: slightly taller than wide (1 : 1.15) or square, clamped to fit available space
        val targetAspect = 1.12f
        val arenaWidth: Float
        val arenaHeight: Float

        if (availHeight / availWidth >= targetAspect) {
            arenaWidth = availWidth
            arenaHeight = availWidth * targetAspect
        } else {
            arenaHeight = availHeight
            arenaWidth = availHeight / targetAspect
        }

        val arenaLeft = (totalWidth - arenaWidth) / 2f + gameState.screenShakeOffset.x
        val arenaTop = (totalHeight - arenaHeight) / 2f + gameState.screenShakeOffset.y
        val arenaCornerRadius = 36f

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { _ -> },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val normDx = dragAmount.x / arenaWidth
                            val normDy = dragAmount.y / arenaHeight
                            gameEngine.onDragDelta(normDx, normDy)
                        }
                    )
                }
        ) {
            // 1. Draw Arena Glass Base
            val arenaRect = RoundRect(
                left = arenaLeft,
                top = arenaTop,
                right = arenaLeft + arenaWidth,
                bottom = arenaTop + arenaHeight,
                cornerRadius = CornerRadius(arenaCornerRadius, arenaCornerRadius)
            )

            val arenaPath = Path().apply { addRoundRect(arenaRect) }

            // Glass fill
            drawPath(
                path = arenaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.arenaGlass,
                        colors.arenaGlass.copy(alpha = 0.5f)
                    ),
                    startY = arenaTop,
                    endY = arenaTop + arenaHeight
                )
            )

            // 2. Faint Cyber Grid
            val gridCols = 8
            val gridRows = 9
            val colStep = arenaWidth / gridCols
            val rowStep = arenaHeight / gridRows

            for (c in 1 until gridCols) {
                val gx = arenaLeft + c * colStep
                drawLine(
                    color = colors.arenaGrid,
                    start = Offset(gx, arenaTop + 10f),
                    end = Offset(gx, arenaTop + arenaHeight - 10f),
                    strokeWidth = 1f
                )
            }
            for (r in 1 until gridRows) {
                val gy = arenaTop + r * rowStep
                drawLine(
                    color = colors.arenaGrid,
                    start = Offset(arenaLeft + 10f, gy),
                    end = Offset(arenaLeft + arenaWidth - 10f, gy),
                    strokeWidth = 1f
                )
            }

            // Central subtle time ripple
            val centerRippleRadius = min(arenaWidth, arenaHeight) * 0.22f + sin(pulsePhase) * 6f
            drawCircle(
                color = colors.arenaGrid.copy(alpha = 0.08f),
                radius = centerRippleRadius,
                center = Offset(arenaLeft + arenaWidth * 0.5f, arenaTop + arenaHeight * 0.5f),
                style = Stroke(width = 1.5f)
            )

            // 3. Base Arena Border
            drawPath(
                path = arenaPath,
                color = colors.arenaBorder,
                style = Stroke(width = 2.5f)
            )

            // 4. Round Countdown / Progress Border
            if (gameState.status == GameStatus.PLAYING || gameState.status == GameStatus.READY) {
                drawProgressBorder(
                    arenaPath = arenaPath,
                    progress = gameState.roundProgress,
                    color = NeonCyan
                )
            }

            // 5. Draw Ghost Trails & Ghost Orbs
            val ghostBaseRadiusPx = CollisionDetector.GHOST_RADIUS * min(arenaWidth, arenaHeight)
            val isGracePeriod = gameState.spawnGracePeriodActive
            val graceAlpha = if (isGracePeriod) {
                (gameState.roundElapsedMs / 400f).coerceIn(0.15f, 1f)
            } else 1f

            gameState.echoes.forEachIndexed { index, echo ->
                val ghostColor = getGhostColor(echo.generation)
                val currentPos = echo.getPositionAt(gameState.roundElapsedMs)
                val ghostScreenX = arenaLeft + currentPos.x * arenaWidth
                val ghostScreenY = arenaTop + currentPos.y * arenaHeight

                // Ghost historic motion trail
                if (ghostTrailsEnabled) {
                    val trailPoints = echo.getRecentTrailOffsets(gameState.roundElapsedMs, 850L)
                    if (trailPoints.size >= 2) {
                        for (i in 0 until trailPoints.size - 1) {
                            val p1 = trailPoints[i]
                            val p2 = trailPoints[i + 1]
                            val segmentFraction = (i.toFloat() / trailPoints.size).coerceIn(0f, 1f)
                            val trailAlpha = segmentFraction * 0.45f * graceAlpha

                            drawLine(
                                color = ghostColor.copy(alpha = trailAlpha),
                                start = Offset(arenaLeft + p1.x * arenaWidth, arenaTop + p1.y * arenaHeight),
                                end = Offset(arenaLeft + p2.x * arenaWidth, arenaTop + p2.y * arenaHeight),
                                strokeWidth = 3f * segmentFraction + 1f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Ghost orb
                drawGhostOrb(
                    center = Offset(ghostScreenX, ghostScreenY),
                    radius = ghostBaseRadiusPx,
                    color = ghostColor,
                    alpha = graceAlpha
                )
            }

            // 6. Draw Particles (Trails, Bursts, Collisions)
            gameEngine.particleManager.draw(this, arenaWidth, arenaHeight, arenaLeft, arenaTop)

            // 7. Draw Player Orb
            val playerRadiusPx = CollisionDetector.PLAYER_RADIUS * min(arenaWidth, arenaHeight)
            val playerScreenX = arenaLeft + gameState.playerPos.x * arenaWidth
            val playerScreenY = arenaTop + gameState.playerPos.y * arenaHeight
            val playerCenter = Offset(playerScreenX, playerScreenY)

            if (gameState.status == GameStatus.COLLAPSE) {
                drawCollapsingPlayer(
                    center = playerCenter,
                    radius = playerRadiusPx,
                    progress = gameState.collapseProgress
                )
            } else {
                drawActivePlayer(
                    center = playerCenter,
                    radius = playerRadiusPx,
                    pulsePhase = pulsePhase
                )
            }

            // 8. Close Call Screen Edge Pulse
            if (gameState.screenEdgePulseAlpha > 0.01f) {
                drawPath(
                    path = arenaPath,
                    color = NeonCyan.copy(alpha = (gameState.screenEdgePulseAlpha * 0.65f).coerceIn(0f, 1f)),
                    style = Stroke(width = 8f)
                )
            }
        }
    }
}

private fun DrawScope.drawProgressBorder(
    arenaPath: Path,
    progress: Float,
    color: Color
) {
    if (progress <= 0.001f) return
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(arenaPath, false)
    val totalLength = pathMeasure.length
    val currentLength = totalLength * progress

    val segmentPath = Path()
    pathMeasure.getSegment(0f, currentLength, segmentPath, true)

    drawPath(
        path = segmentPath,
        color = color,
        style = Stroke(
            width = 3.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawGhostOrb(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    // Outer halo
    drawCircle(
        color = color.copy(alpha = 0.22f * alpha),
        radius = radius * 2.1f,
        center = center
    )
    // Glow
    drawCircle(
        color = color.copy(alpha = 0.55f * alpha),
        radius = radius * 1.35f,
        center = center
    )
    // Core
    drawCircle(
        color = color.copy(alpha = 0.85f * alpha),
        radius = radius * 0.9f,
        center = center
    )
    // Center bright spark
    drawCircle(
        color = Color.White.copy(alpha = 0.75f * alpha),
        radius = radius * 0.35f,
        center = center
    )
}

private fun DrawScope.drawActivePlayer(
    center: Offset,
    radius: Float,
    pulsePhase: Float
) {
    val dynamicPulse = sin(pulsePhase) * 2.2f

    // Outer soft halo
    drawCircle(
        color = PlayerHalo,
        radius = radius * 2.5f + dynamicPulse,
        center = center
    )
    // Cyan glow
    drawCircle(
        color = PlayerCyanGlow,
        radius = radius * 1.55f + dynamicPulse * 0.5f,
        center = center
    )
    // Vivid electric cyan orb
    drawCircle(
        color = PlayerCyan,
        radius = radius,
        center = center
    )
    // High intensity white energy core
    drawCircle(
        color = PlayerCore,
        radius = radius * 0.45f,
        center = center
    )
}

private fun DrawScope.drawCollapsingPlayer(
    center: Offset,
    radius: Float,
    progress: Float
) {
    val flashAlpha = (1f - progress * 1.2f).coerceAtLeast(0f)
    if (flashAlpha > 0f) {
        // Bright white flash
        drawCircle(
            color = Color.White.copy(alpha = flashAlpha),
            radius = radius * (1f + progress * 2.5f),
            center = center
        )
    }

    // Expanding temporal collapse shockwave
    val shockwaveRadius = radius * (1f + progress * 6.5f)
    val ringAlpha = (1f - progress).coerceIn(0f, 1f)
    drawCircle(
        color = NeonCyan.copy(alpha = ringAlpha),
        radius = shockwaveRadius,
        center = center,
        style = Stroke(width = 3.5f * ringAlpha)
    )
}
