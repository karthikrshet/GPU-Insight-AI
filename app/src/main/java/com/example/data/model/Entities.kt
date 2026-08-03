package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cluster_nodes")
data class ClusterNodeEntity(
    @PrimaryKey val id: String,
    val nodeName: String,
    val ipAddress: String,
    val vendor: String, // NVIDIA, AMD, INTEL, APPLE
    val status: String, // ONLINE, DEGRADED, OFFLINE
    val gpuCount: Int,
    val region: String,
    val isPrimary: Boolean = false
)

@Entity(tableName = "gpus")
data class GpuEntity(
    @PrimaryKey val id: String,
    val nodeId: String,
    val gpuIndex: Int,
    val name: String,
    val vendor: String,
    val architecture: String,
    val driverVersion: String,
    val cudaVersion: String,
    val vramTotalMb: Int,
    val powerMaxW: Int,
    val temperatureMaxC: Int,
    val eccEnabled: Boolean = true,
    val migEnabled: Boolean = false,
    val pcieGeneration: String = "PCIe Gen 5 x16"
)

@Entity(tableName = "metric_telemetry")
data class MetricTelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gpuId: String,
    val timestamp: Long,
    val utilizationPct: Float,
    val memoryUsedMb: Int,
    val tempC: Int,
    val powerW: Float,
    val fanSpeedPct: Int,
    val clockGraphicsMhz: Int,
    val clockMemoryMhz: Int,
    val tensorCorePct: Float,
    val pcieThroughputGbps: Float,
    val tokensPerSec: Float = 0f,
    val ttftMs: Float = 0f
)

@Entity(tableName = "processes")
data class ProcessEntity(
    @PrimaryKey val pid: Int,
    val gpuId: String,
    val user: String,
    val appName: String,
    val command: String,
    val vramUsedMb: Int,
    val cpuPct: Float,
    val runtimeSec: Long,
    val status: String = "RUNNING"
)

@Entity(tableName = "alert_rules")
data class AlertRuleEntity(
    @PrimaryKey val id: String,
    val ruleName: String,
    val gpuId: String,
    val metricType: String, // TEMPERATURE, VRAM, POWER, ECC, ANOMALY
    val thresholdValue: Float,
    val channel: String, // SLACK, DISCORD, PAGERDUTY, EMAIL, WEBHOOK
    val enabled: Boolean = true,
    val triggered: Boolean = false,
    val lastTriggeredAt: Long = 0L
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val actor: String,
    val role: String, // OWNER, ADMIN, OPERATOR, VIEWER, AUDITOR
    val action: String, // PROCESS_KILL, NODE_RESTART, ROLE_CHANGE, ALERT_TRIGGER, AI_REDACTION, REPORT_SHARE
    val targetResource: String,
    val details: String,
    val ipAddress: String,
    val status: String // SUCCESS, DENIED
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val title: String,
    val generatedAt: Long,
    val summaryText: String,
    val shareToken: String,
    val costSavingsEst: Float,
    val carbonKg: Float,
    val status: String = "ACTIVE"
)
