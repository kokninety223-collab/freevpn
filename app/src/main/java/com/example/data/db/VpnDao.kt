package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {
    @Query("SELECT * FROM favorite_servers ORDER BY addedAt DESC")
    fun getFavoriteServers(): Flow<List<FavoriteServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteServerEntity)

    @Query("DELETE FROM favorite_servers WHERE serverId = :serverId")
    suspend fun removeFavorite(serverId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_servers WHERE serverId = :serverId)")
    suspend fun isFavorite(serverId: String): Boolean

    @Query("SELECT * FROM vpn_history ORDER BY connectedAt DESC LIMIT 20")
    fun getRecentHistory(): Flow<List<VpnHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: VpnHistoryEntity)

    @Query("DELETE FROM vpn_history")
    suspend fun clearHistory()
}
