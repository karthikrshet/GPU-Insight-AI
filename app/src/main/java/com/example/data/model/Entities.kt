package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gpu_metrics")
data class GpuMetric(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val gpuId: Int,
    val gpuName: String,
    val utilizationPercent: Float,
    val vramUsedMb: Long,
    val vramTotalMb: Long,
    val powerDrawWatts: Float,
    val temperatureCelsius: Float,
    val clockFrequencyMhz: Int,
    val fanSpeedPercent: Float
)

@Entity(tableName = "audit_events")
data class AuditEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val userId: String,
    val action: String,
    val resourceId: String,
    val previousHash: String = "",
    val currentHash: String = ""
)
