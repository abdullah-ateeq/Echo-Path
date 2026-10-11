package com.example.echopath.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Palette - Deep Futuristic Temporal Aesthetic
val VoidDark = Color(0xFF060913)
val SpaceDark = Color(0xFF0B1021)
val ArenaGlassDark = Color(0xFF111936)
val ArenaBorderDark = Color(0xFF233566)
val ArenaGridDark = Color(0x184A70E2)

// Light Palette - Clean Minimalist Cyber
val VoidLight = Color(0xFFF3F5F9)
val SpaceLight = Color(0xFFFFFFFF)
val ArenaGlassLight = Color(0xFFE4E9F2)
val ArenaBorderLight = Color(0xFFC3D0E8)
val ArenaGridLight = Color(0x1F2B4C7E)

// Player Energy Colors
val PlayerCore = Color(0xFFFFFFFF)
val PlayerCyan = Color(0xFF00F5FF)
val PlayerCyanGlow = Color(0x6600F5FF)
val PlayerHalo = Color(0x2200E5FF)

// Controlled Ghost Palette (Cycling by Generation)
val GhostPalette = listOf(
    Color(0xFFA855F7), // Gen 1: Soft Violet
    Color(0xFFF43F5E), // Gen 2: Vibrant Rose / Magenta
    Color(0xFF3B82F6), // Gen 3: Radiant Blue
    Color(0xFFF59E0B), // Gen 4: Warm Amber
    Color(0xFF10B981), // Gen 5: Electric Emerald
    Color(0xFF06B6D4), // Gen 6: Aqua Cyan
    Color(0xFF8B5CF6), // Gen 7: Electric Purple
    Color(0xFFEC4899), // Gen 8: Hot Pink
    Color(0xFFEAB308), // Gen 9: Golden Pulse
    Color(0xFF6366F1), // Gen 10: Indigo Ray
    Color(0xFF14B8A6), // Gen 11: Teal Echo
    Color(0xFFFB7185)  // Gen 12: Coral Flare
)

fun getGhostColor(generation: Int): Color {
    val index = (generation - 1).coerceAtLeast(0) % GhostPalette.size
    return GhostPalette[index]
}

// Accent & UI Colors
val NeonCyan = Color(0xFF00F5FF)
val NeonViolet = Color(0xFFA855F7)
val NeonAmber = Color(0xFFF59E0B)
val NeonRed = Color(0xFFFF3366)
val NeonEmerald = Color(0xFF10B981)

val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextTertiaryLight = Color(0xFF94A3B8)