package com.sethbenkov.voiceai.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_table")
data class Usage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val timestamp: Long = System.currentTimeMillis()
) 