package com.example.data

import com.example.data.model.GpuMetric
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TelemetryPoller @Inject constructor(
    private val repository: GpuInsightRepository
) {
    private var pollingJob: Job? = null

    fun startPolling(scope: CoroutineScope, intervalMs: Long = 500L, gpuCount: Int = 8) {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                repeat(gpuCount) { gpuId -> repository.recordMetric(syntheticMetric(gpuId)) }
                delay(intervalMs)
            }
        }
    }

    fun stopPolling() { pollingJob?.cancel() }

    private fun syntheticMetric(gpuId: Int) = GpuMetric(
        gpuId              = gpuId,
        gpuName            = "NVIDIA H100 SXM5 #$gpuId",
        utilizationPercent = Random.nextFloat() * 30f + 70f,
        vramUsedMb         = 50000L + Random.nextLong(0, 30000),
        vramTotalMb        = 81920L,
        powerDrawWatts     = 400f + Random.nextFloat() * 300f,
        temperatureCelsius = 65f + Random.nextFloat() * 20f,
        clockFrequencyMhz  = 1800 + Random.nextInt(-100, 100),
        fanSpeedPercent    = 60f + Random.nextFloat() * 30f
    )
}
