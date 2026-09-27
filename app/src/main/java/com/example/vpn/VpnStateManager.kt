package com.example.vpn

import com.example.data.DefaultServers
import com.example.model.ConnectionState
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VpnStateManager {
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _selectedServer = MutableStateFlow(DefaultServers.getDefaultServer())
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _sessionStats = MutableStateFlow(VpnSessionStats())
    val sessionStats: StateFlow<VpnSessionStats> = _sessionStats.asStateFlow()

    private val _killSwitch = MutableStateFlow(true)
    val killSwitch: StateFlow<Boolean> = _killSwitch.asStateFlow()

    private val _dnsLeakProtection = MutableStateFlow(true)
    val dnsLeakProtection: StateFlow<Boolean> = _dnsLeakProtection.asStateFlow()

    private val _stealthMode = MutableStateFlow(false)
    val stealthMode: StateFlow<Boolean> = _stealthMode.asStateFlow()

    private val _protocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val protocol: StateFlow<VpnProtocol> = _protocol.asStateFlow()

    private val _autoOptimizeWhenNeeded = MutableStateFlow(true)
    val autoOptimizeWhenNeeded: StateFlow<Boolean> = _autoOptimizeWhenNeeded.asStateFlow()

    private val _optimizationAlert = MutableStateFlow<com.example.model.OptimizationNotification?>(null)
    val optimizationAlert: StateFlow<com.example.model.OptimizationNotification?> = _optimizationAlert.asStateFlow()

    fun setConnectionState(state: ConnectionState) {
        _connectionState.value = state
    }

    fun setSelectedServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun updateSessionStats(stats: VpnSessionStats) {
        _sessionStats.value = stats
    }

    fun resetStats() {
        _sessionStats.value = VpnSessionStats()
    }

    fun setKillSwitch(enabled: Boolean) {
        _killSwitch.value = enabled
    }

    fun setDnsLeakProtection(enabled: Boolean) {
        _dnsLeakProtection.value = enabled
    }

    fun setStealthMode(enabled: Boolean) {
        _stealthMode.value = enabled
    }

    fun setProtocol(proto: VpnProtocol) {
        _protocol.value = proto
    }

    fun setAutoOptimizeWhenNeeded(enabled: Boolean) {
        _autoOptimizeWhenNeeded.value = enabled
    }

    fun setOptimizationAlert(alert: com.example.model.OptimizationNotification?) {
        _optimizationAlert.value = alert
    }

    fun clearOptimizationAlert() {
        _optimizationAlert.value = null
    }
}
