package com.example.data

import com.example.data.db.FavoriteServerEntity
import com.example.data.db.VpnDao
import com.example.data.db.VpnHistoryEntity
import kotlinx.coroutines.flow.Flow

class VpnRepository(private val vpnDao: VpnDao) {

    val favoriteServers: Flow<List<FavoriteServerEntity>> = vpnDao.getFavoriteServers()
    val recentHistory: Flow<List<VpnHistoryEntity>> = vpnDao.getRecentHistory()

    suspend fun toggleFavorite(serverId: String) {
        val exists = vpnDao.isFavorite(serverId)
        if (exists) {
            vpnDao.removeFavorite(serverId)
        } else {
            vpnDao.addFavorite(FavoriteServerEntity(serverId = serverId))
        }
    }

    suspend fun saveHistory(
        serverId: String,
        serverName: String,
        country: String,
        flagEmoji: String,
        durationSeconds: Long,
        bytesDownloaded: Long,
        bytesUploaded: Long
    ) {
        if (durationSeconds > 0) {
            vpnDao.insertHistory(
                VpnHistoryEntity(
                    serverId = serverId,
                    serverName = serverName,
                    country = country,
                    flagEmoji = flagEmoji,
                    durationSeconds = durationSeconds,
                    bytesDownloaded = bytesDownloaded,
                    bytesUploaded = bytesUploaded
                )
            )
        }
    }

    suspend fun clearHistory() {
        vpnDao.clearHistory()
    }
}
