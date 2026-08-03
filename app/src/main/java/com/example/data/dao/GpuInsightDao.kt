package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GpuInsightDao {

    // Cluster Nodes
    @Query("SELECT * FROM cluster_nodes ORDER BY isPrimary DESC, nodeName ASC")
    fun getAllClusterNodes(): Flow<List<ClusterNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClusterNodes(nodes: List<ClusterNodeEntity>)

    // GPUs
    @Query("SELECT * FROM gpus WHERE nodeId = :nodeId ORDER BY gpuIndex ASC")
    fun getGpusForNode(nodeId: String): Flow<List<GpuEntity>>

    @Query("SELECT * FROM gpus ORDER BY gpuIndex ASC")
    fun getAllGpus(): Flow<List<GpuEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpus(gpus: List<GpuEntity>)

    // Metric Telemetry
    @Query("SELECT * FROM metric_telemetry WHERE gpuId = :gpuId ORDER BY timestamp DESC LIMIT 60")
    fun getLatestTelemetryForGpu(gpuId: String): Flow<List<MetricTelemetryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(telemetry: MetricTelemetryEntity)

    // Processes
    @Query("SELECT * FROM processes WHERE gpuId = :gpuId ORDER BY vramUsedMb DESC")
    fun getProcessesForGpu(gpuId: String): Flow<List<ProcessEntity>>

    @Query("SELECT * FROM processes ORDER BY vramUsedMb DESC")
    fun getAllProcesses(): Flow<List<ProcessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcesses(processes: List<ProcessEntity>)

    @Query("DELETE FROM processes WHERE pid = :pid")
    suspend fun deleteProcess(pid: Int)

    // Alert Rules
    @Query("SELECT * FROM alert_rules ORDER BY ruleName ASC")
    fun getAllAlertRules(): Flow<List<AlertRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertRules(rules: List<AlertRuleEntity>)

    @Query("UPDATE alert_rules SET enabled = :enabled WHERE id = :ruleId")
    suspend fun updateAlertRuleStatus(ruleId: String, enabled: Boolean)

    @Query("UPDATE alert_rules SET triggered = :triggered, lastTriggeredAt = :lastTriggeredAt WHERE id = :ruleId")
    suspend fun setAlertRuleTriggered(ruleId: String, triggered: Boolean, lastTriggeredAt: Long)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Reports
    @Query("SELECT * FROM reports ORDER BY generatedAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)
}
