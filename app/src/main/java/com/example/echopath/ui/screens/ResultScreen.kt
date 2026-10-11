package com.example.echopath.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.game.HapticManager
import com.example.echopath.game.SoundManager
import com.example.echopath.model.RunStats
import com.example.echopath.ui.components.ButtonVariant
import com.example.echopath.ui.components.EchoArenaBackground
import com.example.echopath.ui.components.GlassCard
import com.example.echopath.ui.components.NewBestBadge
import com.example.echopath.ui.components.PremiumButton
import com.example.echopath.ui.components.StatItem
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonEmerald
import com.example.echopath.ui.theme.NeonViolet

@Composable
fun ResultScreen(
    stats: RunStats,
    soundManager: SoundManager,
    hapticManager: HapticManager,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalEchoPathColors.current

    LaunchedEffect(stats.isNewBest) {
        if (stats.isNewBest && stats.roundSurvived > 1) {
            soundManager.playNewBest()
        }
    }

    EchoArenaBackground(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RUN COMPLETE",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ECHO COLLISION",
                    color = colors.textPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }

            // Central Score Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                borderGlowColor = if (stats.isNewBest) Color(0x66F59E0B) else Color(0x3300F5FF)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (stats.isNewBest) {
                        NewBestBadge()
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Text(
                        text = "${stats.roundSurvived}",
                        color = colors.accentCyan,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 76.sp
                    )
                    Text(
                        text = if (stats.roundSurvived == 1) "ROUND SURVIVED" else "ROUNDS SURVIVED",
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 2x2 Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatItem(
                            label = "SURVIVAL TIME",
                            value = stats.formattedSurvivalTime,
                            accentColor = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            label = "ECHOES",
                            value = "${stats.echoesCreated}",
                            accentColor = NeonViolet,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatItem(
                            label = "CLOSE CALLS",
                            value = "${stats.closeCalls}",
                            accentColor = NeonEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(
                            label = "BEST RECORD",
                            value = "${stats.bestRound}",
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PremiumButton(
                    text = "PLAY AGAIN",
                    icon = Icons.Rounded.Replay,
                    onClick = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onPlayAgainClick()
                    },
                    variant = ButtonVariant.CYAN,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PremiumButton(
                        text = "HOME",
                        icon = Icons.Rounded.Home,
                        onClick = {
                            soundManager.playClick()
                            hapticManager.vibrateClick()
                            onHomeClick()
                        },
                        variant = ButtonVariant.GLASS,
                        modifier = Modifier.weight(1f)
                    )

                    PremiumButton(
                        text = "SHARE",
                        icon = Icons.Rounded.Share,
                        onClick = {
                            soundManager.playClick()
                            hapticManager.vibrateClick()
                            val shareText = "I survived ${stats.roundSurvived} rounds against my own echoes in Echo Path! Can you beat your past?"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Echo Path Score"))
                        },
                        variant = ButtonVariant.VIOLET,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
