package com.example.echopath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
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
import com.example.echopath.ui.components.HomeDemoArena
import com.example.echopath.ui.components.PremiumButton
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonViolet

@Composable
fun HomeScreen(
    bestRound: Int,
    soundManager: SoundManager,
    hapticManager: HapticManager,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val colors = LocalEchoPathColors.current

    EchoArenaBackground(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Title & Subtitle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ECHO PATH",
                    color = colors.textPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Outrun your own past.",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp
                )
            }

            // Center: Demo Arena
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                HomeDemoArena(modifier = Modifier.fillMaxSize())
            }

            // Bottom Section: CTA, Best Round, Bottom Navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main CTA: PLAY
                PremiumButton(
                    text = "PLAY",
                    icon = Icons.Rounded.PlayArrow,
                    onClick = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onPlayClick()
                    },
                    modifier = Modifier.fillMaxWidth(0.78f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Best Round Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardSurface)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "BEST ROUND",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$bestRound",
                        color = colors.accentCyan,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.cardSurface)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeBottomNavItem(
                        icon = Icons.Rounded.Home,
                        label = "Home",
                        isSelected = true,
                        onClick = { }
                    )
                    HomeBottomNavItem(
                        icon = Icons.Rounded.Settings,
                        label = "Settings",
                        isSelected = false,
                        onClick = {
                            soundManager.playClick()
                            hapticManager.vibrateClick()
                            onSettingsClick()
                        }
                    )
                    HomeBottomNavItem(
                        icon = Icons.Rounded.Info,
                        label = "About",
                        isSelected = false,
                        onClick = {
                            soundManager.playClick()
                            hapticManager.vibrateClick()
                            onAboutClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalEchoPathColors.current
    val activeColor = colors.accentCyan
    val inactiveColor = colors.textTertiary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
