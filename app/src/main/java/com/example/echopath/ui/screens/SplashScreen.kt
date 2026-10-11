package com.example.echopath.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.ui.components.EchoArenaBackground
import com.example.echopath.ui.theme.GhostPalette
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.PlayerCore
import com.example.echopath.ui.theme.PlayerCyan
import com.example.echopath.ui.theme.PlayerCyanGlow
import com.example.echopath.ui.theme.PlayerHalo
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val colors = LocalEchoPathColors.current
    var titleVisible by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleVisible = true
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        delay(150)
        onSplashFinished()
    }

    EchoArenaBackground(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onSplashFinished() }
    ) {
        val t = progress.value * 6.28318f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width * 0.5f
            val centerY = size.height * 0.42f
            val rx = size.width * 0.32f
            val ry = size.height * 0.16f

            fun getPos(timeVal: Float): Offset {
                return Offset(
                    centerX + rx * sin(timeVal * 1.5f),
                    centerY + ry * cos(timeVal * 2.2f)
                )
            }

            // Draw historic trails
            val sampleCount = 35
            for (s in 0 until sampleCount - 1) {
                val t1 = t - (sampleCount - s) * 0.04f
                val t2 = t - (sampleCount - s - 1) * 0.04f
                val p1 = getPos(t1)
                val p2 = getPos(t2)
                val alpha = (s.toFloat() / sampleCount) * 0.6f

                drawLine(
                    color = PlayerCyan.copy(alpha = alpha),
                    start = p1,
                    end = p2,
                    strokeWidth = 3f * (s.toFloat() / sampleCount) + 1f,
                    cap = StrokeCap.Round
                )
            }

            // Ghost 2 (Magenta echo)
            if (progress.value > 0.25f) {
                val g2Alpha = ((progress.value - 0.25f) / 0.3f).coerceIn(0f, 1f)
                val g2Pos = getPos(t - 1.8f)
                drawCircle(GhostPalette[1].copy(alpha = 0.2f * g2Alpha), 22f, g2Pos)
                drawCircle(GhostPalette[1].copy(alpha = 0.6f * g2Alpha), 14f, g2Pos)
                drawCircle(GhostPalette[1].copy(alpha = g2Alpha), 9f, g2Pos)
            }

            // Ghost 1 (Violet echo)
            if (progress.value > 0.1f) {
                val g1Alpha = ((progress.value - 0.1f) / 0.25f).coerceIn(0f, 1f)
                val g1Pos = getPos(t - 0.9f)
                drawCircle(GhostPalette[0].copy(alpha = 0.25f * g1Alpha), 26f, g1Pos)
                drawCircle(GhostPalette[0].copy(alpha = 0.65f * g1Alpha), 16f, g1Pos)
                drawCircle(GhostPalette[0].copy(alpha = g1Alpha), 10f, g1Pos)
            }

            // Live Player Orb
            val livePos = getPos(t)
            drawCircle(PlayerHalo, 38f, livePos)
            drawCircle(PlayerCyanGlow, 24f, livePos)
            drawCircle(PlayerCyan, 15f, livePos)
            drawCircle(PlayerCore, 7f, livePos)
        }

        // Title and Subtitle
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(tween(700)) + scaleIn(tween(700, easing = FastOutSlowInEasing), initialScale = 0.85f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ECHO PATH",
                        color = colors.textPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Your past becomes the obstacle.",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
