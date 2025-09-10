package com.example.data

import com.example.data.dao.GpuInsightDao
import com.example.data.model.AuditEvent
import com.example.data.model.GpuMetric
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpuInsightRepository @Inject constructor(
    private val dao: GpuInsightDao
) {
    fun getRecentMetrics(): Flow<List<GpuMetric>> = dao.getRecentMetrics()
    fun getMetricsForGpu(gpuId: Int): Flow<List<GpuMetric>> = dao.getMetricsForGpu(gpuId)

    suspend fun recordMetric(metric: GpuMetric) = dao.insertMetric(metric)
    suspend fun recordBatch(metrics: List<GpuMetric>) = dao.insertMetrics(metrics)
    suspend fun recordAuditEvent(event: AuditEvent) = dao.insertAuditEvent(event)

    fun getAuditEvents(): Flow<List<AuditEvent>> = dao.getAuditEvents()

    suspend fun pruneMetricsOlderThan(days: Int = 30) {
        val cutoff = System.currentTimeMillis() - (days * 86_400_000L)
        dao.pruneOldMetrics(cutoff)
    }
}
