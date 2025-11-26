package com.example.domain

data class NvlinkStatus(
    val gpuId: Int,
    val linkId: Int,
    val rxBandwidthGbps: Float,
    val txBandwidthGbps: Float,
    val replayErrors: Long,
    val isActive: Boolean
)

class NvlinkBandwidthMonitor {
    fun aggregateBandwidth(links: List<NvlinkStatus>): Float =
        links.filter { it.isActive }.sumOf { it.rxBandwidthGbps.toDouble() }.toFloat()

    fun detectDegradedLinks(links: List<NvlinkStatus>): List<NvlinkStatus> =
        links.filter { it.isActive && it.rxBandwidthGbps < 100f }

    // H100 SXM5: 18 NVLink 4.0 lanes, 900 GB/s bidirectional
    companion object {
        const val H100_MAX_NVLINK_BW_GBPS = 900f
        const val A100_MAX_NVLINK_BW_GBPS = 600f
    }
}
