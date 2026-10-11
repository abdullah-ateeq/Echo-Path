package com.example.echopath.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echopath.ui.theme.NeonCyan
import com.example.echopath.ui.theme.NeonViolet

@Composable
fun GameTopBar(
    currentRound: Int,
    bestRound: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = com.example.echopath.ui.theme.LocalEchoPathColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Best round badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x18A855F7))
                .border(1.dp, Color(0x33A855F7), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "BEST",
                    color = Color(0xFFA855F7),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "$bestRound",
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Current round indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ROUND",
                color = colors.accentCyan.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "$currentRound",
                color = colors.accentCyan,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Pause button
        NeonIconButton(
            icon = Icons.Rounded.Pause,
            onClick = onPauseClick,
            contentDescription = "Pause Game",
            glowColor = colors.textPrimary,
            size = 44.dp,
            iconSize = 22.dp
        )
    }
}
