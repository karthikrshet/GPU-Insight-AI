package com.example.domain

import com.example.data.model.GpuMetric

data class MemoryLeakAlert(
    val gpuId: Int,
    val detectedAt: Long,
    val vramGrowthMbPerHour: Float,
    val projectedExhaustionHours: Float,
    val recommendation: String
)

class MemoryLeakDetector {
    private val history = mutableMapOf<Int, ArrayDeque<GpuMetric>>()
    private val window = 60

    fun addMetric(metric: GpuMetric) {
        history.getOrPut(metric.gpuId) { ArrayDeque() }.apply {
            addLast(metric)
            if (size > window) removeFirst()
        }
    }

    fun detectLeaks(): List<MemoryLeakAlert> = history.mapNotNull { (gpuId, q) ->
        if (q.size < 10) return@mapNotNull null
        val durationH = (q.last().timestamp - q.first().timestamp) / 3_600_000f
        if (durationH < 0.01f) return@mapNotNull null
        val growthRate = (q.last().vramUsedMb - q.first().vramUsedMb) / durationH
        if (growthRate < 500f) return@mapNotNull null
        val remaining = q.last().vramTotalMb - q.last().vramUsedMb
        MemoryLeakAlert(
            gpuId, System.currentTimeMillis(), growthRate,
            remaining / growthRate,
            "Potential CUDA memory leak on GPU #$gpuId. Check for missing .detach() calls."
        )
    }
}
