package com.example.echopath.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.echopath.ui.theme.GhostPalette
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.PlayerCore
import com.example.echopath.ui.theme.PlayerCyan
import com.example.echopath.ui.theme.PlayerCyanGlow
import com.example.echopath.ui.theme.PlayerHalo
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun HomeDemoArena(modifier: Modifier = Modifier) {
    val colors = LocalEchoPathColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "homeDemo")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "demoTime"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val size = min(width, height) * 0.92f
        val left = (width - size) / 2f
        val top = (height - size) / 2f
        val cornerRadius = 32f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val rect = RoundRect(
                left = left,
                top = top,
                right = left + size,
                bottom = top + size,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
            val path = Path().apply { addRoundRect(rect) }

            // Glass background
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    listOf(colors.arenaGlass, colors.arenaGlass.copy(alpha = 0.4f)),
                    startY = top,
                    endY = top + size
                )
            )

            // Grid lines
            val gridStep = size / 6f
            for (i in 1..5) {
                val gx = left + i * gridStep
                drawLine(
                    color = colors.arenaGrid,
                    start = Offset(gx, top + 8f),
                    end = Offset(gx, top + size - 8f),
                    strokeWidth = 1f
                )
                val gy = top + i * gridStep
                drawLine(
                    color = colors.arenaGrid,
                    start = Offset(left + 8f, gy),
                    end = Offset(left + size - 8f, gy),
                    strokeWidth = 1f
                )
            }

            // Arena border
            drawPath(
                path = path,
                color = colors.arenaBorder,
                style = Stroke(width = 2.2f)
            )

            // Calculate parametric positions
            // Live player orb
            val playerNorm = Offset(
                0.5f + 0.32f * sin(time * 1.4f),
                0.5f + 0.28f * cos(time * 2.1f)
            )

            // Ghost 1 (Violet) - delayed copy
            val ghost1Norm = Offset(
                0.5f + 0.32f * sin((time - 1.4f) * 1.4f),
                0.5f + 0.28f * cos((time - 1.4f) * 2.1f)
            )

            // Ghost 2 (Magenta) - alternative loop
            val ghost2Norm = Offset(
                0.5f + 0.28f * cos((time - 2.8f) * 1.8f),
                0.5f + 0.32f * sin((time - 2.8f) * 1.2f)
            )

            val baseRadius = size * 0.04f

            // Helper to draw trails
            fun drawSampledTrail(color: Color, getPosAt: (Float) -> Offset) {
                val sampleCount = 20
                for (s in 0 until sampleCount - 1) {
                    val t1 = time - (sampleCount - s) * 0.05f
                    val t2 = time - (sampleCount - s - 1) * 0.05f
                    val p1 = getPosAt(t1)
                    val p2 = getPosAt(t2)
                    val alpha = (s.toFloat() / sampleCount) * 0.5f

                    drawLine(
                        color = color.copy(alpha = alpha),
                        start = Offset(left + p1.x * size, top + p1.y * size),
                        end = Offset(left + p2.x * size, top + p2.y * size),
                        strokeWidth = (s.toFloat() / sampleCount) * 4f + 1f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw Ghost 2 (Magenta) Trail & Orb
            drawSampledTrail(GhostPalette[1]) { t ->
                Offset(0.5f + 0.28f * cos((t - 2.8f) * 1.8f), 0.5f + 0.32f * sin((t - 2.8f) * 1.2f))
            }
            val g2Screen = Offset(left + ghost2Norm.x * size, top + ghost2Norm.y * size)
            drawCircle(GhostPalette[1].copy(alpha = 0.25f), baseRadius * 1.9f, g2Screen)
            drawCircle(GhostPalette[1].copy(alpha = 0.65f), baseRadius * 1.2f, g2Screen)
            drawCircle(GhostPalette[1], baseRadius * 0.8f, g2Screen)
            drawCircle(Color.White.copy(alpha = 0.8f), baseRadius * 0.3f, g2Screen)

            // Draw Ghost 1 (Violet) Trail & Orb
            drawSampledTrail(GhostPalette[0]) { t ->
                Offset(0.5f + 0.32f * sin((t - 1.4f) * 1.4f), 0.5f + 0.28f * cos((t - 1.4f) * 2.1f))
            }
            val g1Screen = Offset(left + ghost1Norm.x * size, top + ghost1Norm.y * size)
            drawCircle(GhostPalette[0].copy(alpha = 0.25f), baseRadius * 1.9f, g1Screen)
            drawCircle(GhostPalette[0].copy(alpha = 0.65f), baseRadius * 1.2f, g1Screen)
            drawCircle(GhostPalette[0], baseRadius * 0.8f, g1Screen)
            drawCircle(Color.White.copy(alpha = 0.8f), baseRadius * 0.3f, g1Screen)

            // Draw Live Player Trail & Orb
            drawSampledTrail(PlayerCyan) { t ->
                Offset(0.5f + 0.32f * sin(t * 1.4f), 0.5f + 0.28f * cos(t * 2.1f))
            }
            val playerScreen = Offset(left + playerNorm.x * size, top + playerNorm.y * size)
            drawCircle(PlayerHalo, baseRadius * 2.3f, playerScreen)
            drawCircle(PlayerCyanGlow, baseRadius * 1.45f, playerScreen)
            drawCircle(PlayerCyan, baseRadius * 0.95f, playerScreen)
            drawCircle(PlayerCore, baseRadius * 0.45f, playerScreen)
        }
    }
}
