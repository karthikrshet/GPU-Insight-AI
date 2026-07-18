package com.example.domain

data class PcieStats(
    val gpuId: Int,
    val generation: Int,
    val lanes: Int,
    val txThroughputGbps: Float,
    val rxThroughputGbps: Float,
    val maxBandwidthGbps: Float
)

object PcieBandwidthMonitor {
    private val GEN_BW = mapOf(3 to 32f, 4 to 64f, 5 to 128f)

    fun getMaxBandwidth(gen: Int, lanes: Int = 16): Float =
        (GEN_BW[gen] ?: 32f) * (lanes / 16f)

    fun calculateUtilization(stats: PcieStats): Float =
        ((stats.txThroughputGbps + stats.rxThroughputGbps) / (stats.maxBandwidthGbps * 2f))
            .coerceIn(0f, 1f)

    fun isBottleneck(stats: PcieStats, threshold: Float = 0.8f): Boolean =
        calculateUtilization(stats) > threshold
}
