package com.example.domain

import com.example.data.GpuInsightRepository
import com.example.data.model.GpuMetric
import com.example.network.GeminiApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGpuMetricsUseCase @Inject constructor(
    private val repository: GpuInsightRepository
) {
    operator fun invoke(): Flow<List<GpuMetric>> = repository.getRecentMetrics()
}

class GetGpuHealthUseCase @Inject constructor(
    private val repository: GpuInsightRepository
) {
    operator fun invoke(gpuId: Int): Flow<GpuHealth> =
        repository.getMetricsForGpu(gpuId).map { metrics ->
            metrics.firstOrNull()?.let { GpuHealthCalculator.calculate(it) } ?: GpuHealth.UNKNOWN
        }
}

class AnalyzeGpuErrorUseCase @Inject constructor(
    private val geminiService: GeminiApiService
) {
    suspend operator fun invoke(stackTrace: String): Result<String> =
        geminiService.analyzeGpuError(stackTrace)
}

enum class GpuHealth { HEALTHY, WARNING, CRITICAL, UNKNOWN }
