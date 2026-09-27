package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ConnectionState
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald

@Composable
fun VpnPowerButton(
    connectionState: ConnectionState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kruger_pulse")

    // Subtle gentle pulse when connected
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Smooth rotation for connecting indicator
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "spin"
    )

    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(connectionState) {
        scaleAnim.animateTo(0.95f, animationSpec = tween(120))
        scaleAnim.animateTo(1.0f, animationSpec = tween(180))
    }

    val isConnected = connectionState == ConnectionState.CONNECTED
    val isConnecting = connectionState == ConnectionState.CONNECTING || connectionState == ConnectionState.DISCONNECTING

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(220.dp)
            .scale(if (isConnected) pulseScale else scaleAnim.value)
    ) {
        // Minimalist Ambient Glow (soft & subtle)
        Box(
            modifier = Modifier
                .size(210.dp)
                .drawBehind {
                    val glowColor = when {
                        isConnected -> NeonEmerald.copy(alpha = 0.18f)
                        isConnecting -> NeonAmber.copy(alpha = 0.15f)
                        else -> Color(0x0AFFFFFF)
                    }
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(glowColor, Color.Transparent),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2
                        )
                    )
                }
        )

        // Minimalist Outer Track Ring
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = when {
                    isConnected -> NeonEmerald.copy(alpha = 0.4f)
                    else -> Color(0xFF1E2430)
                },
                radius = size.minDimension / 2f,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // Connecting arc
        if (isConnecting) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .rotate(rotation)
            ) {
                drawArc(
                    color = NeonAmber,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Main Minimalist Button with Realistic Logo
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(184.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            when {
                                isConnected -> Color(0xFF0F231D)
                                isConnecting -> Color(0xFF241C10)
                                else -> Color(0xFF141923)
                            },
                            Color(0xFF090D14)
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, radius = 92.dp),
                    onClick = onToggle
                )
                .testTag("vpn_power_button")
        ) {
            // Realistic KRUGER Logo as centerpiece
            Image(
                painter = painterResource(id = R.drawable.img_kruger_logo),
                contentDescription = "KRUGER VPN Toggle",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(126.dp)
            )
        }
    }
}
