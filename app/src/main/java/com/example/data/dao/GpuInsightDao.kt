package com.example.data.dao

import androidx.room.*
import com.example.data.model.GpuMetric
import com.example.data.model.AuditEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface GpuInsightDao {
    @Query("SELECT * FROM gpu_metrics ORDER BY timestamp DESC LIMIT 100")
    fun getRecentMetrics(): Flow<List<GpuMetric>>

    @Query("SELECT * FROM gpu_metrics WHERE gpuId = :gpuId ORDER BY timestamp DESC LIMIT 500")
    fun getMetricsForGpu(gpuId: Int): Flow<List<GpuMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: GpuMetric)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: List<GpuMetric>)

    @Query("SELECT * FROM audit_events ORDER BY timestamp DESC")
    fun getAuditEvents(): Flow<List<AuditEvent>>

    @Insert
    suspend fun insertAuditEvent(event: AuditEvent)

    @Query("DELETE FROM gpu_metrics WHERE timestamp < :cutoff")
    suspend fun pruneOldMetrics(cutoff: Long)
}
