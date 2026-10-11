package com.example.echopath.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.game.HapticManager
import com.example.echopath.game.SoundManager
import com.example.echopath.model.GameSettings
import com.example.echopath.model.ThemeMode
import com.example.echopath.ui.components.ButtonVariant
import com.example.echopath.ui.components.ConfirmDialog
import com.example.echopath.ui.components.EchoArenaBackground
import com.example.echopath.ui.components.NeonIconButton
import com.example.echopath.ui.components.PremiumButton
import com.example.echopath.ui.components.SegmentedThemeSelector
import com.example.echopath.ui.components.SettingSwitchRow
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan

@Composable
fun SettingsScreen(
    settings: GameSettings,
    soundManager: SoundManager,
    hapticManager: HapticManager,
    onThemeModeChange: (ThemeMode) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onParticlesToggle: (Boolean) -> Unit,
    onGhostTrailsToggle: (Boolean) -> Unit,
    onReducedEffectsToggle: (Boolean) -> Unit,
    onResetBestScore: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = LocalEchoPathColors.current
    var showResetDialog by remember { mutableStateOf(false) }
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
                    text = "SETTINGS",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Scrollable Settings Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Section
                SettingsSectionHeader(title = "APPEARANCE")

                SegmentedThemeSelector(
                    selectedOption = settings.themeMode.name,
                    options = listOf("DARK", "LIGHT", "SYSTEM"),
                    onOptionSelected = { option ->
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        val mode = runCatching { ThemeMode.valueOf(option) }.getOrDefault(ThemeMode.DARK)
                        onThemeModeChange(mode)
                    }
                )

                // Audio & Feedback
                SettingsSectionHeader(title = "AUDIO & FEEDBACK")

                SettingSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Procedural temporal synthesized audio",
                    checked = settings.soundEnabled,
                    onCheckedChange = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onSoundToggle(it)
                    }
                )

                SettingSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibrations for movements, echoes, and collisions",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onHapticsToggle(it)
                    }
                )

                // Visual Effects
                SettingsSectionHeader(title = "VISUALS & PERFORMANCE")

                SettingSwitchRow(
                    title = "Particles",
                    subtitle = "Canvas energy trails, collision sparks, and bursts",
                    checked = settings.particlesEnabled,
                    onCheckedChange = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onParticlesToggle(it)
                    }
                )

                SettingSwitchRow(
                    title = "Ghost Trails",
                    subtitle = "Show faint historical paths trailing behind echoes",
                    checked = settings.ghostTrailsEnabled,
                    onCheckedChange = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onGhostTrailsToggle(it)
                    }
                )

                SettingSwitchRow(
                    title = "Reduced Effects",
                    subtitle = "Simplify animations and reduce particle density",
                    checked = settings.reducedEffects,
                    onCheckedChange = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        onReducedEffectsToggle(it)
                    }
                )

                // Data Reset Section
                SettingsSectionHeader(title = "DATA & RECORDS")

                PremiumButton(
                    text = "RESET BEST SCORE",
                    icon = Icons.Rounded.DeleteOutline,
                    onClick = {
                        soundManager.playClick()
                        hapticManager.vibrateClick()
                        showResetDialog = true
                    },
                    variant = ButtonVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showResetDialog) {
            ConfirmDialog(
                title = "Reset Record?",
                message = "Are you sure you want to reset your best round record? This action cannot be undone.",
                confirmText = "RESET",
                cancelText = "CANCEL",
                onConfirm = {
                    onResetBestScore()
                },
                onDismiss = {
                    showResetDialog = false
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    val colors = LocalEchoPathColors.current
    Text(
        text = title,
        color = colors.accentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}
