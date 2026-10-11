package com.example.echopath.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.echopath.model.ThemeMode

data class EchoPathCustomColors(
    val voidBackground: Color,
    val spaceSurface: Color,
    val arenaGlass: Color,
    val arenaBorder: Color,
    val arenaGrid: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentCyan: Color,
    val accentViolet: Color
)

val LocalEchoPathColors = staticCompositionLocalOf {
    EchoPathCustomColors(
        voidBackground = VoidDark,
        spaceSurface = SpaceDark,
        arenaGlass = ArenaGlassDark,
        arenaBorder = ArenaBorderDark,
        arenaGrid = ArenaGridDark,
        cardSurface = Color(0x18FFFFFF),
        cardBorder = Color(0x22FFFFFF),
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        accentCyan = NeonCyan,
        accentViolet = NeonViolet
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    secondary = NeonViolet,
    onSecondary = Color.White,
    tertiary = NeonAmber,
    background = VoidDark,
    onBackground = TextPrimaryDark,
    surface = SpaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = ArenaGlassDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = ArenaBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0099B8),
    onPrimary = Color.White,
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    tertiary = Color(0xFFD97706),
    background = VoidLight,
    onBackground = TextPrimaryLight,
    surface = SpaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = ArenaGlassLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = ArenaBorderLight
)

@Composable
fun EchoPathTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val customColors = if (isDark) {
        EchoPathCustomColors(
            voidBackground = VoidDark,
            spaceSurface = SpaceDark,
            arenaGlass = ArenaGlassDark,
            arenaBorder = ArenaBorderDark,
            arenaGrid = ArenaGridDark,
            cardSurface = Color(0x18FFFFFF),
            cardBorder = Color(0x22FFFFFF),
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            textTertiary = TextTertiaryDark,
            accentCyan = NeonCyan,
            accentViolet = NeonViolet
        )
    } else {
        EchoPathCustomColors(
            voidBackground = VoidLight,
            spaceSurface = SpaceLight,
            arenaGlass = ArenaGlassLight,
            arenaBorder = ArenaBorderLight,
            arenaGrid = ArenaGridLight,
            cardSurface = Color(0xFFFFFFFF),
            cardBorder = Color(0xFFD5DEEC),
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textTertiary = TextTertiaryLight,
            accentCyan = Color(0xFF0099B8),
            accentViolet = Color(0xFF7C3AED)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = customColors.voidBackground.toArgb()
                window.navigationBarColor = customColors.voidBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalEchoPathColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}