package com.example.domain

import com.example.data.model.GpuMetric

data class DefragSuggestion(
    val gpuId: Int,
    val fragmentationEstimatePercent: Float,
    val action: String,
    val expectedSavingsMb: Long
)

object VramDefragAdvisor {
    fun analyze(metrics: List<GpuMetric>): List<DefragSuggestion> =
        metrics.mapNotNull { m ->
            val ratio = m.vramUsedMb.toFloat() / m.vramTotalMb
            if (ratio < 0.7f) return@mapNotNull null
            val frag = (ratio - 0.7f) * 50f
            DefragSuggestion(
                gpuId = m.gpuId,
                fragmentationEstimatePercent = frag,
                action = when {
                    ratio > 0.95f -> "Critical: call torch.cuda.empty_cache() immediately"
                    ratio > 0.85f -> "Warning: set max_split_size_mb=512 in PYTORCH_CUDA_ALLOC_CONF"
                    else          -> "Monitor: watch for OOM patterns"
                },
                expectedSavingsMb = (m.vramTotalMb * frag / 100).toLong()
            )
        }
}
