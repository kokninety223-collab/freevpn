package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_servers")
data class FavoriteServerEntity(
    @PrimaryKey val serverId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vpn_history")
data class VpnHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serverId: String,
    val serverName: String,
    val country: String,
    val flagEmoji: String,
    val connectedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Long,
    val bytesDownloaded: Long,
    val bytesUploaded: Long
)
