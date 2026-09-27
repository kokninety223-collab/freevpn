package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.model.ConnectionState
import com.example.model.OptimizationNotification
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.ui.components.VpnPowerButton
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    connectionState: ConnectionState,
    selectedServer: VpnServer,
    sessionStats: VpnSessionStats,
    onToggleVpn: () -> Unit,
    onNavigateToServers: () -> Unit,
    onNavigateToSpeedTest: () -> Unit,
    onSelectBestAsia: () -> Unit,
    onSelectBestUsa: () -> Unit,
    onUpdateToBestNow: () -> Unit,
    onDismissAlert: () -> Unit,
    bestAsiaServer: VpnServer? = null,
    bestUsaServer: VpnServer? = null,
    optimizationAlert: OptimizationNotification? = null,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    val isConnecting = connectionState == ConnectionState.CONNECTING || connectionState == ConnectionState.DISCONNECTING

    // Formatted stats
    val downSpeedStr = if (sessionStats.downSpeedKbps >= 1024f) {
        "%.1f MB/s".format(sessionStats.downSpeedKbps / 1024f)
    } else {
        "%.0f KB/s".format(sessionStats.downSpeedKbps)
    }

    val upSpeedStr = if (sessionStats.upSpeedKbps >= 1024f) {
        "%.1f MB/s".format(sessionStats.upSpeedKbps / 1024f)
    } else {
        "%.0f KB/s".format(sessionStats.upSpeedKbps)
    }

    val hours = sessionStats.durationSeconds / 3600
    val mins = (sessionStats.durationSeconds / 60) % 60
    val secs = sessionStats.durationSeconds % 60
    val durationStr = "%02d:%02d:%02d".format(hours, mins, secs)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Minimalist Clean Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KRUGER",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Secure Network",
                    fontSize = 12.sp,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
            }

            // Minimalist Status Indicator Dot & Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF141923))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isConnected -> NeonEmerald
                                isConnecting -> NeonAmber
                                else -> Color(0xFF6B7280)
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isConnected -> "Connected"
                        isConnecting -> "Connecting"
                        else -> "Disconnected"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isConnected -> NeonEmerald
                        isConnecting -> NeonAmber
                        else -> TextSecondary
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Minimalist Hero: Center Realistic Logo Button
        VpnPowerButton(
            connectionState = connectionState,
            onToggle = onToggleVpn,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Connection State Heading
        Text(
            text = when {
                isConnected -> "Connected & Protected"
                isConnecting -> "Securing Connection..."
                else -> "Not Connected"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when {
                isConnected -> "Encrypted tunnel active • Virtual IP: ${sessionStats.virtualIp}"
                isConnecting -> "Routing through ${selectedServer.name}..."
                else -> "Tap the emblem or button below to connect"
            },
            fontSize = 13.sp,
            color = if (isConnected) NeonEmerald else TextMuted
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Minimalist Primary Action Button (Connect / Disconnect)
        if (isConnected) {
            OutlinedButton(
                onClick = onToggleVpn,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AlertRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(46.dp)
                    .testTag("disconnect_action_button")
            ) {
                Text(
                    text = "DISCONNECT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Button(
                onClick = onToggleVpn,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnecting) Color(0xFF263238) else NeonCyan,
                    contentColor = if (isConnecting) NeonAmber else Color(0xFF070B12)
                ),
                enabled = !isConnecting,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(46.dp)
                    .testTag("connect_action_button")
            ) {
                Text(
                    text = if (isConnecting) "CONNECTING..." else "CONNECT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Minimalist Selected Server Card
        Text(
            text = "SERVER LOCATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(1.dp, Color(0xFF1E2430), RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onNavigateToServers
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("select_server_button")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedServer.flagEmoji,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = selectedServer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NeonEmerald)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${selectedServer.country} • ${selectedServer.pingMs} ms",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Change Server",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Minimalist Real-Time Stats (Only displayed when connected)
        AnimatedVisibility(
            visible = isConnected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF121722))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DOWNLOAD", fontSize = 10.sp, color = TextMuted, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = downSpeedStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(Color(0xFF263042))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("UPLOAD", fontSize = 10.sp, color = TextMuted, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = upSpeedStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(Color(0xFF263042))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DURATION", fontSize = 10.sp, color = TextMuted, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = durationStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
