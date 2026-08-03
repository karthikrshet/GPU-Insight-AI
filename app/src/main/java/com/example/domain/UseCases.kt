package com.example.domain

import com.example.data.model.GpuEntity
import com.example.data.model.MetricTelemetryEntity
import com.example.data.model.ProcessEntity
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture Domain UseCases for GPU Insight AI
 */

interface GetGpuTelemetryUseCase {
    suspend operator fun invoke(gpuId: String): Flow<List<MetricTelemetryEntity>>
}

interface SyncRemoteAgentGpuUseCase {
    suspend operator fun invoke(agentEndpoint: String): Result<Boolean>
}

interface AnalyzeGpuStacktraceUseCase {
    suspend operator fun invoke(stacktrace: String, promptTemplate: String): String
}

interface TerminateGpuProcessUseCase {
    suspend operator fun invoke(gpuId: String, pid: Int, mfaToken: String): Result<Boolean>
}

/**
 * Client-Server Remote GPU Agent Connection Config
 */
data class RemoteGpuAgentConfig(
    val agentId: String,
    val host: String,
    val port: Int,
    val useTls: Boolean = true,
    val apiKey: String? = null,
    val syncIntervalMs: Long = 1000L
)
