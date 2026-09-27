package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerRegion
import com.example.model.VpnServer
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SelectedServerCard(
    server: VpnServer,
    onClickChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurface)
            .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClickChange
            )
            .padding(16.dp)
            .testTag("selected_server_card"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Flag Box
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(1.dp, CyberBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    RegionBadge(region = server.region)
                    if (server.badge != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        ServerBadgeTag(badge = server.badge)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.city,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = " • ",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${server.pingMs} ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (server.pingMs < 60) NeonCyan else if (server.pingMs < 110) NeonEmerald else NeonAmber
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Change Server",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun ServerListItem(
    server: VpnServer,
    isSelected: Boolean,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) NeonCyan else CyberBorder
    val bgColor = if (isSelected) CyberSurfaceVariant else CyberSurface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onSelect
            )
            .padding(14.dp)
            .testTag("server_item_${server.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CyberDarkBg(isSelected))
                .border(1.dp, if (isSelected) NeonCyan.copy(alpha = 0.5f) else CyberBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = server.flagEmoji,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Details
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = server.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) NeonCyan else TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                RegionBadge(region = server.region)
                if (server.badge != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    ServerBadgeTag(badge = server.badge)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Load Bar & Latency
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${server.pingMs} ms",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (server.pingMs < 60) NeonCyan else if (server.pingMs < 110) NeonEmerald else NeonAmber
                )
                Spacer(modifier = Modifier.width(10.dp))

                LinearProgressIndicator(
                    progress = { server.loadPercentage / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (server.loadPercentage < 50) NeonEmerald else if (server.loadPercentage < 75) NeonAmber else Color(0xFFEF4444),
                    trackColor = CyberBorder
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${server.loadPercentage}%",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Favorite Toggle
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "Toggle Favorite",
                tint = if (isFavorite) NeonAmber else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RegionBadge(region: ServerRegion) {
    val (text, bg, textColor) = when (region) {
        ServerRegion.ASIA -> Triple("ASIA", Color(0xFF00382E), NeonCyan)
        ServerRegion.USA -> Triple("USA", Color(0xFF00325A), ElectricBlue)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ServerBadgeTag(badge: String) {
    val (bgColor, textColor) = when (badge) {
        "BEST ASIA" -> Pair(Color(0xFF00382E), NeonCyan)
        "BEST USA" -> Pair(Color(0xFF00325A), ElectricBlue)
        "FASTEST" -> Pair(Color(0xFF452200), NeonAmber)
        "LOW PING" -> Pair(Color(0xFF003D2A), NeonEmerald)
        else -> Pair(CyberSurfaceVariant, TextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = badge,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun BestServerHighlightCard(
    title: String,
    server: VpnServer,
    isSelected: Boolean,
    onSelectAndConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (server.region == ServerRegion.ASIA) NeonCyan else ElectricBlue

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurface)
            .border(
                1.dp,
                if (isSelected) accentColor else CyberBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onSelectAndConnect
            )
            .padding(12.dp)
            .testTag("best_server_card_${server.region.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = server.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (server.pingMs < 60) NeonCyan else NeonEmerald)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${server.pingMs} ms",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (server.pingMs < 60) NeonCyan else NeonEmerald
                )
            }

            Text(
                text = "Load ${server.loadPercentage}%",
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

private fun CyberDarkBg(isSelected: Boolean): Color {
    return if (isSelected) Color(0xFF0F2633) else Color(0xFF0A0F1D)
}
