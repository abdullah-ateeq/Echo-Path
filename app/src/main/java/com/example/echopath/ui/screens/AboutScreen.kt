package com.example.echopath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.game.HapticManager
import com.example.echopath.game.SoundManager
import com.example.echopath.ui.components.EchoArenaBackground
import com.example.echopath.ui.components.GlassCard
import com.example.echopath.ui.components.NeonIconButton
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonEmerald
import com.example.echopath.ui.theme.NeonViolet

@Composable
fun AboutScreen(
    soundManager: SoundManager,
    hapticManager: HapticManager,
    onBackClick: () -> Unit
) {
    val colors = LocalEchoPathColors.current
    val scrollState = rememberScrollState()

    EchoArenaBackground(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onBackClick()
                    },
                    contentDescription = "Back",
                    glowColor = colors.textPrimary
                )

                Text(
                    text = "ABOUT",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Scrollable About Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Game Overview Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    borderGlowColor = Color(0x4400F5FF)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ECHO PATH",
                            color = colors.accentCyan,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"Every route you create returns as a ghost. Survive the next round without colliding with your own past.\"",
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // How to Play Section
                Text(
                    text = "HOW TO PLAY",
                    color = NeonViolet,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                HowToPlayStep(
                    icon = Icons.Rounded.TouchApp,
                    stepNumber = "01",
                    title = "Drag to Move",
                    description = "Touch anywhere in the arena to steer your glowing cyan orb smoothly.",
                    accentColor = NeonCyan
                )

                HowToPlayStep(
                    icon = Icons.Rounded.History,
                    stepNumber = "02",
                    title = "Survive Each Round",
                    description = "Stay alive inside the contained arena until the round border completes.",
                    accentColor = Color(0xFF38BDF8)
                )

                HowToPlayStep(
                    icon = Icons.Rounded.AllInclusive,
                    stepNumber = "03",
                    title = "Your Route Returns",
                    description = "When time expires, your path becomes a permanent replaying echo.",
                    accentColor = NeonViolet
                )

                HowToPlayStep(
                    icon = Icons.Rounded.WarningAmber,
                    stepNumber = "04",
                    title = "Avoid Every Ghost",
                    description = "Each new round replays all previous paths in sync. Avoid touching any ghost!",
                    accentColor = Color(0xFFF59E0B)
                )

                // Privacy & Offline Info Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    borderGlowColor = Color(0x22FFFFFF)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Echo Path works fully offline and does not require an account.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Version Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VERSION 1.0.0",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HowToPlayStep(
    icon: ImageVector,
    stepNumber: String,
    title: String,
    description: String,
    accentColor: Color
) {
    val colors = LocalEchoPathColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.cardSurface)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stepNumber,
                    color = colors.textTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = colors.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
