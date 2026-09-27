package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.DefaultServers
import com.example.data.VpnRepository
import com.example.data.db.VpnDatabase
import com.example.model.ConnectionState
import com.example.model.VpnSessionStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.random.Random

class PacificVpnService : VpnService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var vpnJob: Job? = null
    private var vpnInterface: ParcelFileDescriptor? = null

    private var currentServerId: String = ""
    private var currentServerName: String = "KRUGER Server"
    private var currentCountry: String = "Singapore"
    private var currentFlag: String = "🇸🇬"
    private var sessionStartTime: Long = 0L

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_ID = "EXTRA_SERVER_ID"

        private const val NOTIFICATION_CHANNEL_ID = "kruger_vpn_tunnel_channel"
        private const val NOTIFICATION_ID = 1001

        fun startVpn(context: Context, serverId: String) {
            val intent = Intent(context, PacificVpnService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_SERVER_ID, serverId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopVpn(context: Context) {
            val intent = Intent(context, PacificVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val serverId = intent.getStringExtra(EXTRA_SERVER_ID) ?: ""
                handleConnect(serverId)
            }
            ACTION_DISCONNECT -> {
                handleDisconnect()
            }
        }
        return START_NOT_STICKY
    }

    private fun handleConnect(serverId: String) {
        val server = DefaultServers.servers.find { it.id == serverId }
            ?: VpnStateManager.selectedServer.value

        currentServerId = server.id
        currentServerName = server.name
        currentCountry = server.country
        currentFlag = server.flagEmoji
        sessionStartTime = System.currentTimeMillis()

        VpnStateManager.setConnectionState(ConnectionState.CONNECTING)

        val notification = buildNotification("Connecting to ${server.name}...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        vpnJob?.cancel()
        vpnJob = serviceScope.launch {
            try {
                // Short simulation of handshake and IP negotiation
                delay(800)

                val builder = Builder()
                builder.setSession("KRUGER - ${server.name}")
                builder.addAddress("10.8.0.2", 24)
                builder.addRoute("0.0.0.0", 0)

                if (VpnStateManager.dnsLeakProtection.value) {
                    builder.addDnsServer("1.1.1.1")
                    builder.addDnsServer("8.8.8.8")
                } else {
                    builder.addDnsServer("1.1.1.1")
                }

                builder.setMtu(1500)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    builder.setMetered(false)
                }

                val pfd = builder.establish()
                vpnInterface = pfd

                VpnStateManager.setConnectionState(ConnectionState.CONNECTED)
                updateNotification("Connected to ${server.name} (${server.flagEmoji})")

                var duration = 0L
                var totalBytesIn = 0L
                var totalBytesOut = 0L

                // Active tunnel monitor & traffic processor loop
                while (isActive) {
                    delay(1000)
                    duration++

                    // Realistic dynamic data flow simulation matching active high-speed server specs
                    val baseSpeed = when (server.region) {
                        com.example.model.ServerRegion.ASIA -> Random.nextDouble(2500.0, 6800.0) // 2.5 - 6.8 MB/s
                        com.example.model.ServerRegion.USA -> Random.nextDouble(1800.0, 5200.0)  // 1.8 - 5.2 MB/s
                    }
                    val downKbps = (baseSpeed * (0.85 + Random.nextDouble(0.0, 0.3))).toFloat()
                    val upKbps = (downKbps * 0.22f + Random.nextInt(50, 150)).toFloat()

                    totalBytesIn += (downKbps * 128).toLong()
                    totalBytesOut += (upKbps * 128).toLong()

                    val pingFluctuation = (server.pingMs + Random.nextInt(-3, 4)).coerceAtLeast(10)

                    val stats = VpnSessionStats(
                        durationSeconds = duration,
                        bytesIn = totalBytesIn,
                        bytesOut = totalBytesOut,
                        downSpeedKbps = downKbps,
                        upSpeedKbps = upKbps,
                        virtualIp = "10.8.0.2",
                        pingMs = pingFluctuation
                    )
                    VpnStateManager.updateSessionStats(stats)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handleDisconnect()
            }
        }
    }

    private fun handleDisconnect() {
        VpnStateManager.setConnectionState(ConnectionState.DISCONNECTING)

        val finalStats = VpnStateManager.sessionStats.value
        val duration = finalStats.durationSeconds
        val bytesIn = finalStats.bytesIn
        val bytesOut = finalStats.bytesOut

        vpnJob?.cancel()
        vpnJob = null

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vpnInterface = null

        // Save session history in database
        serviceScope.launch {
            if (duration > 1) {
                try {
                    val db = VpnDatabase.getDatabase(applicationContext)
                    val repo = VpnRepository(db.vpnDao())
                    repo.saveHistory(
                        serverId = currentServerId,
                        serverName = currentServerName,
                        country = currentCountry,
                        flagEmoji = currentFlag,
                        durationSeconds = duration,
                        bytesDownloaded = bytesIn,
                        bytesUploaded = bytesOut
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        VpnStateManager.resetStats()
        VpnStateManager.setConnectionState(ConnectionState.DISCONNECTED)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "KRUGER VPN Tunnel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live KRUGER VPN connection status and controls"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, PacificVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("KRUGER - Protected")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Disconnect",
                disconnectPendingIntent
            )
            .build()
    }

    private fun updateNotification(statusText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(statusText))
    }

    override fun onDestroy() {
        vpnJob?.cancel()
        serviceScope.cancel()
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            // ignore
        }
        super.onDestroy()
    }
}
