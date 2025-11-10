package com.example.domain

import com.example.data.GpuInsightRepository
import com.example.data.model.GpuMetric
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

class ChaosEngineUseCase @Inject constructor(
    private val repository: GpuInsightRepository
) {
    suspend fun simulateThermalSpike(gpuId: Int, durationMs: Long = 30_000L) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < durationMs) {
            repository.recordMetric(GpuMetric(
                gpuId              = gpuId,
                gpuName            = "GPU #$gpuId [CHAOS]",
                utilizationPercent = Random.nextFloat() * 15f + 85f,
                vramUsedMb         = 79872L,
                vramTotalMb        = 81920L,
                powerDrawWatts     = Random.nextFloat() * 50f + 650f,
                temperatureCelsius = Random.nextFloat() * 10f + 88f,
                clockFrequencyMhz  = 1200,
                fanSpeedPercent    = 100f
            ))
            delay(500)
        }
    }

    suspend fun simulateOOM(gpuId: Int) {
        repository.recordMetric(GpuMetric(
            gpuId = gpuId, gpuName = "GPU #$gpuId [OOM]",
            utilizationPercent = 100f, vramUsedMb = 81920L, vramTotalMb = 81920L,
            powerDrawWatts = 700f, temperatureCelsius = 95f,
            clockFrequencyMhz = 800, fanSpeedPercent = 100f
        ))
    }
}
