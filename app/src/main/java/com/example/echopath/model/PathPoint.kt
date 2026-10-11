package com.example.echopath.model

import androidx.compose.ui.geometry.Offset

data class PathPoint(
    val x: Float,
    val y: Float,
    val timeMs: Long
) {
    fun toOffset(): Offset = Offset(x, y)
}
