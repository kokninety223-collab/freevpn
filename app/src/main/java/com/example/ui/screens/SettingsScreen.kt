package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.VpnHistoryEntity
import com.example.model.VpnProtocol
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    killSwitch: Boolean,
    dnsLeakProtection: Boolean,
    stealthMode: Boolean,
    autoOptimizeWhenNeeded: Boolean,
    selectedProtocol: VpnProtocol,
    historyList: List<VpnHistoryEntity>,
    onToggleKillSwitch: (Boolean) -> Unit,
    onToggleDnsLeakProtection: (Boolean) -> Unit,
    onToggleStealthMode: (Boolean) -> Unit,
    onToggleAutoOptimizeWhenNeeded: (Boolean) -> Unit,
    onSelectProtocol: (VpnProtocol) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_kruger_logo),
                    contentDescription = "KRUGER",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "KRUGER SETTINGS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Security, protocols & session logs",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Security Toggles
        Text(
            text = "SECURITY & PRIVACY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
        ) {
            SettingSwitchItem(
                icon = Icons.Default.Shield,
                title = "Kill Switch",
                subtitle = "Block all internet if VPN drops unexpectedly",
                checked = killSwitch,
                onCheckedChange = onToggleKillSwitch
            )

            HorizontalDivider(color = CyberBorder)

            SettingSwitchItem(
                icon = Icons.Default.Lock,
                title = "DNS Leak Protection",
                subtitle = "Prevent ISP from spying on DNS queries",
                checked = dnsLeakProtection,
                onCheckedChange = onToggleDnsLeakProtection
            )

            HorizontalDivider(color = CyberBorder)

            SettingSwitchItem(
                icon = Icons.Default.Security,
                title = "Stealth Mode (Obfuscation)",
                subtitle = "Disguise traffic as normal HTTPS to bypass firewalls",
                checked = stealthMode,
                onCheckedChange = onToggleStealthMode
            )

            HorizontalDivider(color = CyberBorder)

            SettingSwitchItem(
                icon = Icons.Default.Public,
                title = "Auto-Update to Best Server When Needed",
                subtitle = "Automatically re-evaluate and switch to optimal Asia/USA server if latency rises",
                checked = autoOptimizeWhenNeeded,
                onCheckedChange = onToggleAutoOptimizeWhenNeeded
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Protocol Selection
        Text(
            text = "TUNNEL PROTOCOL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
        ) {
            VpnProtocol.entries.forEachIndexed { index, proto ->
                ProtocolSelectionRow(
                    protocol = proto,
                    isSelected = selectedProtocol == proto,
                    onSelect = { onSelectProtocol(proto) }
                )
                if (index < VpnProtocol.entries.size - 1) {
                    HorizontalDivider(color = CyberBorder)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Connection History
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT SESSIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            if (historyList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = onClearHistory
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = AlertRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clear",
                        fontSize = 11.sp,
                        color = AlertRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No connection sessions yet",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
            ) {
                historyList.take(5).forEachIndexed { index, item ->
                    HistoryItemRow(history = item)
                    if (index < historyList.take(5).size - 1) {
                        HorizontalDivider(color = CyberBorder)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (checked) NeonCyan.copy(alpha = 0.15f) else CyberSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) NeonCyan else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00382E),
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = CyberSurfaceVariant
            )
        )
    }
}

@Composable
private fun ProtocolSelectionRow(
    protocol: VpnProtocol,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = NeonCyan,
                unselectedColor = TextMuted
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = protocol.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) NeonCyan else TextPrimary
            )
            Text(
                text = protocol.description,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun HistoryItemRow(history: VpnHistoryEntity) {
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(history.connectedAt))
    val mbTotal = (history.bytesDownloaded + history.bytesUploaded) / (1024f * 1024f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = history.flagEmoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = history.serverName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "$dateStr • ${formatDuration(history.durationSeconds)}",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Text(
            text = String.format(Locale.US, "%.1f MB", mbTotal),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = NeonCyan
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
}
