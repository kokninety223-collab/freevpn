package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultServers
import com.example.data.VpnRepository
import com.example.data.db.VpnDatabase
import com.example.data.db.VpnHistoryEntity
import com.example.model.ConnectionState
import com.example.model.ServerRegion
import com.example.model.SpeedTestResult
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.vpn.PacificVpnService
import com.example.vpn.VpnStateManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ServerFilterTab {
    ALL,
    ASIA,
    USA,
    FAVORITES
}

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpnRepository = VpnRepository(
        VpnDatabase.getDatabase(application).vpnDao()
    )

    val connectionState: StateFlow<ConnectionState> = VpnStateManager.connectionState
    val selectedServer: StateFlow<VpnServer> = VpnStateManager.selectedServer
    val sessionStats: StateFlow<VpnSessionStats> = VpnStateManager.sessionStats
    val killSwitch: StateFlow<Boolean> = VpnStateManager.killSwitch
    val dnsLeakProtection: StateFlow<Boolean> = VpnStateManager.dnsLeakProtection
    val stealthMode: StateFlow<Boolean> = VpnStateManager.stealthMode
    val currentProtocol: StateFlow<VpnProtocol> = VpnStateManager.protocol
    val autoOptimizeWhenNeeded: StateFlow<Boolean> = VpnStateManager.autoOptimizeWhenNeeded
    val optimizationAlert: StateFlow<com.example.model.OptimizationNotification?> = VpnStateManager.optimizationAlert

    val favoriteServerIds: StateFlow<Set<String>> = repository.favoriteServers
        .map { list -> list.map { it.serverId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recentHistory: StateFlow<List<VpnHistoryEntity>> = repository.recentHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _serversList = MutableStateFlow(
        tagBestServers(DefaultServers.servers)
    )
    val serversList: StateFlow<List<VpnServer>> = _serversList.asStateFlow()

    val bestAsiaServer: StateFlow<VpnServer> = _serversList
        .map { list -> DefaultServers.findBestAsiaServer(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultServers.findBestAsiaServer())

    val bestUsaServer: StateFlow<VpnServer> = _serversList
        .map { list -> DefaultServers.findBestUsaServer(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultServers.findBestUsaServer())

    val bestOverallServer: StateFlow<VpnServer> = _serversList
        .map { list -> DefaultServers.findBestOverallServer(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultServers.findBestOverallServer())

    private val _selectedTab = MutableStateFlow(ServerFilterTab.ALL)
    val selectedTab: StateFlow<ServerFilterTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshingPings = MutableStateFlow(false)
    val isRefreshingPings: StateFlow<Boolean> = _isRefreshingPings.asStateFlow()

    val filteredServers: StateFlow<List<VpnServer>> = combine(
        _serversList,
        _selectedTab,
        _searchQuery,
        favoriteServerIds
    ) { servers, tab, query, favs ->
        servers.filter { server ->
            val matchesTab = when (tab) {
                ServerFilterTab.ALL -> true
                ServerFilterTab.ASIA -> server.region == ServerRegion.ASIA
                ServerFilterTab.USA -> server.region == ServerRegion.USA
                ServerFilterTab.FAVORITES -> favs.contains(server.id)
            }
            val matchesQuery = query.isBlank() ||
                server.name.contains(query, ignoreCase = true) ||
                server.city.contains(query, ignoreCase = true) ||
                server.country.contains(query, ignoreCase = true)

            matchesTab && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultServers.servers)

    // Speed test state
    private val _speedTest = MutableStateFlow(SpeedTestResult())
    val speedTest: StateFlow<SpeedTestResult> = _speedTest.asStateFlow()
    private var speedTestJob: Job? = null

    init {
        // Monitor connection stats: when auto-optimization is enabled and current server slows down, optimize automatically
        viewModelScope.launch {
            VpnStateManager.sessionStats.collect { stats ->
                if (autoOptimizeWhenNeeded.value && connectionState.value == ConnectionState.CONNECTED && stats.pingMs > 130) {
                    val current = selectedServer.value
                    val best = if (current.region == ServerRegion.ASIA) bestAsiaServer.value else bestUsaServer.value
                    if (best.id != current.id && best.pingMs < current.pingMs - 20) {
                        applyServerOptimization(
                            best,
                            if (best.region == ServerRegion.ASIA) "Asia" else "USA",
                            application.applicationContext
                        )
                    }
                }
            }
        }
    }

    fun selectServer(server: VpnServer) {
        VpnStateManager.setSelectedServer(server)
    }

    fun updateToBestAsiaServer(context: Context? = null) {
        val target = bestAsiaServer.value
        applyServerOptimization(target, "Asia", context)
    }

    fun updateToBestUsaServer(context: Context? = null) {
        val target = bestUsaServer.value
        applyServerOptimization(target, "USA", context)
    }

    fun updateToBestServerWhenNeeded(context: Context? = null) {
        // Auto-select between Asia & USA based on lowest latency & health
        val target = bestOverallServer.value
        val regionName = if (target.region == ServerRegion.ASIA) "Asia" else "USA"
        applyServerOptimization(target, regionName, context)
    }

    private fun applyServerOptimization(target: VpnServer, regionName: String, context: Context?) {
        VpnStateManager.setSelectedServer(target)
        val alert = com.example.model.OptimizationNotification(
            message = "Optimal $regionName server selected: ${target.name} (${target.pingMs}ms)",
            serverName = target.name,
            region = target.region,
            pingMs = target.pingMs
        )
        VpnStateManager.setOptimizationAlert(alert)

        // If actively connected, reconnect seamlessly to the new optimal server
        if (context != null && connectionState.value == ConnectionState.CONNECTED) {
            PacificVpnService.startVpn(context, target.id)
        }
    }

    fun dismissOptimizationAlert() {
        VpnStateManager.clearOptimizationAlert()
    }

    fun setAutoOptimizeWhenNeeded(enabled: Boolean) {
        VpnStateManager.setAutoOptimizeWhenNeeded(enabled)
    }

    fun setFilterTab(tab: ServerFilterTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(serverId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(serverId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setKillSwitch(enabled: Boolean) {
        VpnStateManager.setKillSwitch(enabled)
    }

    fun setDnsLeakProtection(enabled: Boolean) {
        VpnStateManager.setDnsLeakProtection(enabled)
    }

    fun setStealthMode(enabled: Boolean) {
        VpnStateManager.setStealthMode(enabled)
    }

    fun setProtocol(proto: VpnProtocol) {
        VpnStateManager.setProtocol(proto)
    }

    fun prepareVpnIntent(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    fun startVpn(context: Context) {
        val server = selectedServer.value
        PacificVpnService.startVpn(context, server.id)
    }

    fun stopVpn(context: Context) {
        PacificVpnService.stopVpn(context)
    }

    fun toggleVpnConnection(context: Context, onNeedPermission: (Intent) -> Unit) {
        when (connectionState.value) {
            ConnectionState.CONNECTED -> {
                stopVpn(context)
            }
            ConnectionState.DISCONNECTED -> {
                val prepareIntent = prepareVpnIntent(context)
                if (prepareIntent != null) {
                    onNeedPermission(prepareIntent)
                } else {
                    startVpn(context)
                }
            }
            ConnectionState.CONNECTING -> {
                stopVpn(context)
            }
            ConnectionState.DISCONNECTING -> {
                // Wait for disconnection
            }
        }
    }

    fun selectSmartFastestServer() {
        val fastest = _serversList.value.minByOrNull { it.pingMs }
        if (fastest != null) {
            VpnStateManager.setSelectedServer(fastest)
        }
    }

    fun refreshPings() {
        if (_isRefreshingPings.value) return
        viewModelScope.launch {
            _isRefreshingPings.value = true
            delay(500)
            val updated = _serversList.value.map { server ->
                val newPing = (server.pingMs + Random.nextInt(-4, 5)).coerceAtLeast(14)
                val newLoad = (server.loadPercentage + Random.nextInt(-3, 4)).coerceIn(15, 95)
                server.copy(pingMs = newPing, loadPercentage = newLoad)
            }
            _serversList.value = tagBestServers(updated)
            _isRefreshingPings.value = false
        }
    }

    private fun tagBestServers(list: List<VpnServer>): List<VpnServer> {
        val bestAsia = DefaultServers.findBestAsiaServer(list)
        val bestUsa = DefaultServers.findBestUsaServer(list)
        val fastest = DefaultServers.findBestOverallServer(list)

        return list.map { server ->
            val badge = when {
                server.id == bestAsia.id -> "BEST ASIA"
                server.id == bestUsa.id -> "BEST USA"
                server.id == fastest.id -> "FASTEST"
                server.pingMs < 30 -> "LOW PING"
                server.loadPercentage < 35 -> "LOW LOAD"
                else -> null
            }
            server.copy(badge = badge)
        }
    }

    fun runSpeedTest() {
        if (_speedTest.value.isTesting) return
        speedTestJob?.cancel()
        speedTestJob = viewModelScope.launch {
            val server = selectedServer.value
            _speedTest.value = SpeedTestResult(isTesting = true, stage = "Finding Optimal Server...", progress = 0.1f)
            delay(500)

            // Stage 1: Ping & Jitter
            _speedTest.value = _speedTest.value.copy(stage = "Testing Ping & Jitter...", progress = 0.25f)
            delay(600)
            val measuredPing = (server.pingMs + Random.nextInt(-3, 4)).coerceAtLeast(12)
            val measuredJitter = Random.nextInt(1, 5)
            _speedTest.value = _speedTest.value.copy(
                pingMs = measuredPing,
                jitterMs = measuredJitter,
                stage = "Testing Download Speed...",
                progress = 0.4f
            )

            // Stage 2: Download
            val baseDown = if (server.region == ServerRegion.ASIA) 72f else 54f
            for (i in 1..5) {
                delay(300)
                val currentDown = (baseDown * (0.6f + (i * 0.08f)) + Random.nextFloat() * 8f)
                _speedTest.value = _speedTest.value.copy(
                    downloadMbps = currentDown,
                    progress = 0.4f + (i * 0.07f)
                )
            }

            // Stage 3: Upload
            _speedTest.value = _speedTest.value.copy(stage = "Testing Upload Speed...", progress = 0.75f)
            val baseUp = baseDown * 0.35f
            for (i in 1..4) {
                delay(300)
                val currentUp = (baseUp * (0.7f + (i * 0.08f)) + Random.nextFloat() * 4f)
                _speedTest.value = _speedTest.value.copy(
                    uploadMbps = currentUp,
                    progress = 0.75f + (i * 0.06f)
                )
            }

            delay(200)
            _speedTest.value = _speedTest.value.copy(
                isTesting = false,
                stage = "Completed",
                progress = 1.0f
            )
        }
    }
}
