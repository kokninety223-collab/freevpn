package com.example.model

enum class ServerRegion {
    ASIA,
    USA
}

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class VpnProtocol(val displayName: String, val description: String) {
    WIREGUARD("WireGuard", "Modern, ultra-fast & lightweight (Recommended)"),
    OPENVPN_UDP("OpenVPN (UDP)", "Fast performance optimized for gaming & streaming"),
    OPENVPN_TCP("OpenVPN (TCP)", "Reliable connection for restrictive networks"),
    STEALTH("Stealth Mode", "Obfuscated tunnel to bypass deep packet inspection")
}

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val city: String,
    val region: ServerRegion,
    val ip: String,
    val port: Int = 51820,
    val pingMs: Int,
    val loadPercentage: Int,
    val flagEmoji: String,
    val protocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val isFree: Boolean = true,
    val isRecommended: Boolean = false,
    val badge: String? = null
)

data class OptimizationNotification(
    val message: String,
    val serverName: String,
    val region: ServerRegion,
    val pingMs: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class VpnSessionStats(
    val durationSeconds: Long = 0,
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val downSpeedKbps: Float = 0f,
    val upSpeedKbps: Float = 0f,
    val virtualIp: String = "10.8.0.2",
    val pingMs: Int = 0
)

data class SpeedTestResult(
    val pingMs: Int = 0,
    val jitterMs: Int = 0,
    val downloadMbps: Float = 0f,
    val uploadMbps: Float = 0f,
    val isTesting: Boolean = false,
    val progress: Float = 0f,
    val stage: String = "Ready"
)
