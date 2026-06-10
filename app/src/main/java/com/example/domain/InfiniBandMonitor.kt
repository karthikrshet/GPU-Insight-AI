package com.example.domain

data class IbPort(
    val nodeId: Int,
    val portId: Int,
    val speedGbps: Int,    // 200=HDR, 400=NDR
    val rxBytes: Long,
    val txBytes: Long,
    val linkErrors: Int,
    val isActive: Boolean
)

data class IbClusterStats(
    val totalNodes: Int,
    val activeLinks: Int,
    val aggregateBandwidthGbps: Float,
    val linkErrorCount: Int,
    val isHealthy: Boolean
)

object InfiniBandMonitor {
    fun aggregateStats(ports: List<IbPort>): IbClusterStats {
        val active = ports.filter { it.isActive }
        return IbClusterStats(
            totalNodes             = ports.map { it.nodeId }.toSet().size,
            activeLinks            = active.size,
            aggregateBandwidthGbps = active.sumOf { it.speedGbps.toDouble() }.toFloat(),
            linkErrorCount         = ports.sumOf { it.linkErrors },
            isHealthy              = ports.all { it.linkErrors < 10 }
        )
    }

    fun detectDegraded(ports: List<IbPort>): List<IbPort> =
        ports.filter { it.isActive && (it.linkErrors > 5 || it.speedGbps < 200) }
}
