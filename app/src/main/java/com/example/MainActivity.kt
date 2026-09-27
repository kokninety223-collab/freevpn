package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ConnectionState
import com.example.ui.VpnViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    HOME("Connect", Icons.Default.Security),
    SERVERS("Servers", Icons.Default.Public),
    SPEED_TEST("Speed", Icons.Default.Speed),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PacificVpnApp()
            }
        }
    }
}

@Composable
fun PacificVpnApp(
    viewModel: VpnViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppNavDestination.HOME) }

    // Observe State
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServer.collectAsStateWithLifecycle()
    val bestAsiaServer by viewModel.bestAsiaServer.collectAsStateWithLifecycle()
    val bestUsaServer by viewModel.bestUsaServer.collectAsStateWithLifecycle()
    val optimizationAlert by viewModel.optimizationAlert.collectAsStateWithLifecycle()
    val sessionStats by viewModel.sessionStats.collectAsStateWithLifecycle()
    val filteredServers by viewModel.filteredServers.collectAsStateWithLifecycle()
    val favoriteServerIds by viewModel.favoriteServerIds.collectAsStateWithLifecycle()
    val selectedFilterTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshingPings by viewModel.isRefreshingPings.collectAsStateWithLifecycle()
    val speedTestResult by viewModel.speedTest.collectAsStateWithLifecycle()
    val killSwitch by viewModel.killSwitch.collectAsStateWithLifecycle()
    val dnsLeakProtection by viewModel.dnsLeakProtection.collectAsStateWithLifecycle()
    val stealthMode by viewModel.stealthMode.collectAsStateWithLifecycle()
    val autoOptimizeWhenNeeded by viewModel.autoOptimizeWhenNeeded.collectAsStateWithLifecycle()
    val currentProtocol by viewModel.currentProtocol.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()

    // Activity Result Launcher for VPN System Confirmation Dialog
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.startVpn(context)
        }
    }

    // Request Notification permission for Foreground Service on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Handle Android system back press to return to HOME
    BackHandler(enabled = currentDestination != AppNavDestination.HOME) {
        currentDestination = AppNavDestination.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                AppNavDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00382E),
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.HOME -> {
                    HomeScreen(
                        connectionState = connectionState,
                        selectedServer = selectedServer,
                        bestAsiaServer = bestAsiaServer,
                        bestUsaServer = bestUsaServer,
                        optimizationAlert = optimizationAlert,
                        sessionStats = sessionStats,
                        onToggleVpn = {
                            viewModel.toggleVpnConnection(context) { intent ->
                                vpnPrepareLauncher.launch(intent)
                            }
                        },
                        onNavigateToServers = {
                            currentDestination = AppNavDestination.SERVERS
                        },
                        onNavigateToSpeedTest = {
                            currentDestination = AppNavDestination.SPEED_TEST
                        },
                        onSelectBestAsia = {
                            viewModel.updateToBestAsiaServer(context)
                        },
                        onSelectBestUsa = {
                            viewModel.updateToBestUsaServer(context)
                        },
                        onUpdateToBestNow = {
                            viewModel.updateToBestServerWhenNeeded(context)
                        },
                        onDismissAlert = {
                            viewModel.dismissOptimizationAlert()
                        }
                    )
                }

                AppNavDestination.SERVERS -> {
                    ServersScreen(
                        servers = filteredServers,
                        selectedServer = selectedServer,
                        bestAsiaServer = bestAsiaServer,
                        bestUsaServer = bestUsaServer,
                        selectedTab = selectedFilterTab,
                        searchQuery = searchQuery,
                        favoriteServerIds = favoriteServerIds,
                        isRefreshingPings = isRefreshingPings,
                        onSelectServer = { server ->
                            viewModel.selectServer(server)
                            currentDestination = AppNavDestination.HOME
                        },
                        onToggleFavorite = { serverId ->
                            viewModel.toggleFavorite(serverId)
                        },
                        onTabSelected = { tab ->
                            viewModel.setFilterTab(tab)
                        },
                        onSearchQueryChange = { query ->
                            viewModel.setSearchQuery(query)
                        },
                        onRefreshPings = {
                            viewModel.refreshPings()
                        }
                    )
                }

                AppNavDestination.SPEED_TEST -> {
                    SpeedTestScreen(
                        speedTest = speedTestResult,
                        selectedServer = selectedServer,
                        onStartTest = {
                            viewModel.runSpeedTest()
                        }
                    )
                }

                AppNavDestination.SETTINGS -> {
                    SettingsScreen(
                        killSwitch = killSwitch,
                        dnsLeakProtection = dnsLeakProtection,
                        stealthMode = stealthMode,
                        autoOptimizeWhenNeeded = autoOptimizeWhenNeeded,
                        selectedProtocol = currentProtocol,
                        historyList = recentHistory,
                        onToggleKillSwitch = { viewModel.setKillSwitch(it) },
                        onToggleDnsLeakProtection = { viewModel.setDnsLeakProtection(it) },
                        onToggleStealthMode = { viewModel.setStealthMode(it) },
                        onToggleAutoOptimizeWhenNeeded = { viewModel.setAutoOptimizeWhenNeeded(it) },
                        onSelectProtocol = { viewModel.setProtocol(it) },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        }
    }
}
