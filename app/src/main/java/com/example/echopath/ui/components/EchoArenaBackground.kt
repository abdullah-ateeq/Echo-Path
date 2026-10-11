package com.example.echopath.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.VoidDark

@Composable
fun EchoArenaBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val colors = LocalEchoPathColors.current

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Deep background gradient
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        colors.voidBackground,
                        colors.spaceSurface,
                        colors.voidBackground
                    )
                )
            )

            // Subtle ambient radial glow at top and bottom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1800F5FF), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.15f),
                    radius = size.width * 0.65f
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x14A855F7), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.85f),
                    radius = size.width * 0.7f
                )
            )
        }

        content()
    }
}
