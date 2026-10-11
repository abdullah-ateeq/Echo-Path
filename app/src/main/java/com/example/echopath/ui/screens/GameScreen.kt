package com.example.echopath.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.game.GameEngine
import com.example.echopath.model.GameStatus
import com.example.echopath.ui.components.ArenaCanvas
import com.example.echopath.ui.components.ButtonVariant
import com.example.echopath.ui.components.EchoArenaBackground
import com.example.echopath.ui.components.GameTopBar
import com.example.echopath.ui.components.GlassCard
import com.example.echopath.ui.components.PremiumButton
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonAmber
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonEmerald
import com.example.echopath.ui.theme.NeonViolet

@Composable
fun GameScreen(
    gameEngine: GameEngine,
    ghostTrailsEnabled: Boolean,
    onGameOver: () -> Unit,
    onHomeClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    val colors = LocalEchoPathColors.current
    val gameState by gameEngine.gameState.collectAsState()

    // Intercept back button to pause or exit
    BackHandler {
        if (gameState.status == GameStatus.PLAYING || gameState.status == GameStatus.READY) {
            gameEngine.pause()
        } else if (gameState.status == GameStatus.PAUSED) {
            onHomeClick()
        }
    }

    // Precise display-synced frame loop using withFrameNanos
    LaunchedEffect(gameState.status) {
        if (gameState.status == GameStatus.GAME_OVER) {
            onGameOver()
            return@LaunchedEffect
        }

        var lastFrameNanos = 0L
        while (gameState.status != GameStatus.PAUSED && gameState.status != GameStatus.GAME_OVER) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameNanos != 0L) {
                    val dtSec = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f
                    gameEngine.update(dtSec)
                }
                lastFrameNanos = frameTimeNanos
            }
        }
    }

    EchoArenaBackground(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Best, Round, Pause Button
            GameTopBar(
                currentRound = gameState.roundNumber,
                bestRound = gameState.bestRound,
                onPauseClick = { gameEngine.pause() }
            )

            // Main Interactive Arena Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                ArenaCanvas(
                    gameEngine = gameEngine,
                    gameState = gameState,
                    ghostTrailsEnabled = ghostTrailsEnabled,
                    modifier = Modifier.fillMaxSize()
                )

                // Round / Echo Banner Announcement
                if (gameState.bannerText != null) {
                    val isClean = gameState.bannerText == "CLEAN LOOP"
                    val isEchoCreated = gameState.bannerText == "ECHO CREATED"
                    val bannerColor = when {
                        isClean -> NeonEmerald
                        isEchoCreated -> NeonViolet
                        else -> NeonCyan
                    }

                    Column(
                        modifier = Modifier
                            .scale(gameState.bannerScale)
                            .padding(bottom = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = gameState.bannerText!!,
                            color = bannerColor.copy(alpha = gameState.bannerAlpha),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.5.sp
                        )

                        if (gameState.bannerSubText != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gameState.bannerSubText!!,
                                color = colors.textPrimary.copy(alpha = gameState.bannerAlpha * 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }

                // In-game Tutorial Hint pill (when banner is not showing)
                if (gameState.bannerText == null && gameState.tutorialHint != null && gameState.roundNumber <= 2) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = gameState.tutorialHint!!,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }
        }

        // Pause Modal Overlay
        AnimatedVisibility(
            visible = gameState.status == GameStatus.PAUSED,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x8A000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.84f)
                        .padding(20.dp),
                    cornerRadius = 28.dp,
                    borderGlowColor = Color(0x4400F5FF)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PAUSED",
                            color = colors.textPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        PremiumButton(
                            text = "RESUME",
                            onClick = { gameEngine.resume() },
                            variant = ButtonVariant.CYAN,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        PremiumButton(
                            text = "RESTART",
                            onClick = onRestartClick,
                            variant = ButtonVariant.VIOLET,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        PremiumButton(
                            text = "HOME",
                            onClick = onHomeClick,
                            variant = ButtonVariant.GLASS,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
