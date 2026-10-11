package com.example.echopath.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.example.echopath.ui.theme.LocalEchoPathColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderGlowColor: Color = Color(0x3300F5FF),
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalEchoPathColors.current
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.cardSurface)
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(
                        borderGlowColor,
                        colors.cardBorder
                    )
                ),
                shape
            )
            .padding(20.dp),
        content = content
    )
}
