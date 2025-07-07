package com.sethbenkov.voiceai.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    @Insert
    suspend fun insert(usage: Usage)

    @Query("SELECT * FROM usage_table ORDER BY timestamp DESC")
    fun getAllUsage(): Flow<List<Usage>>
} 