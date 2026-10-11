package com.example.echopath.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.ui.theme.LocalEchoPathColors
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonViolet

enum class ButtonVariant {
    CYAN,
    VIOLET,
    GLASS
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.CYAN,
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(horizontal = 28.dp, vertical = 16.dp)
) {
    val colors = LocalEchoPathColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "btnScale"
    )

    val shape = RoundedCornerShape(20.dp)

    val backgroundBrush = when (variant) {
        ButtonVariant.CYAN -> Brush.horizontalGradient(
            listOf(colors.accentCyan, Color(0xFF00B4D8))
        )
        ButtonVariant.VIOLET -> Brush.horizontalGradient(
            listOf(colors.accentViolet, Color(0xFF7928CA))
        )
        ButtonVariant.GLASS -> Brush.horizontalGradient(
            listOf(colors.cardSurface, colors.cardSurface)
        )
    }

    val contentColor = when (variant) {
        ButtonVariant.CYAN -> Color(0xFF020E18)
        ButtonVariant.VIOLET -> Color.White
        ButtonVariant.GLASS -> colors.textPrimary
    }

    val borderStroke = when (variant) {
        ButtonVariant.CYAN -> Brush.horizontalGradient(
            listOf(Color(0xFFE0FFFF), colors.accentCyan)
        )
        ButtonVariant.VIOLET -> Brush.horizontalGradient(
            listOf(Color(0xFFFFD6FF), colors.accentViolet)
        )
        ButtonVariant.GLASS -> Brush.horizontalGradient(
            listOf(colors.cardBorder, colors.cardBorder)
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (variant != ButtonVariant.GLASS) (if (isPressed) 6.dp else 12.dp) else 0.dp,
                shape = shape,
                ambientColor = if (variant == ButtonVariant.CYAN) NeonCyan else NeonViolet,
                spotColor = if (variant == ButtonVariant.CYAN) NeonCyan else NeonViolet
            )
            .clip(shape)
            .background(backgroundBrush)
            .border(1.5.dp, borderStroke, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun NeonIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    glowColor: Color = NeonCyan,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp
) {
    val colors = LocalEchoPathColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "iconBtnScale"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(shape)
            .background(colors.cardSurface)
            .border(1.dp, colors.cardBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = glowColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
