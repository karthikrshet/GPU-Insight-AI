package com.example.domain

data class NvlinkEdge(
    val fromGpuId: Int,
    val toGpuId: Int,
    val linkCount: Int,
    val bandwidthGbps: Float,
    val isHealthy: Boolean
)

data class NvlinkTopologyGraph(
    val gpuCount: Int,
    val edges: List<NvlinkEdge>,
    val totalAggregateBandwidthGbps: Float
)

class NvlinkTopologyBuilder {
    fun build(connections: Map<Pair<Int, Int>, Float>): NvlinkTopologyGraph {
        val edges = connections.entries.map { (pair, bw) ->
            NvlinkEdge(pair.first, pair.second, (bw / 25f).toInt(), bw, bw > 0f)
        }
        val gpuCount = connections.keys.flatMap { listOf(it.first, it.second) }.toSet().size
        return NvlinkTopologyGraph(gpuCount, edges, edges.sumOf { it.bandwidthGbps.toDouble() }.toFloat())
    }

    companion object {
        // H100 SXM5: 900 GB/s bidirectional NVLink 4.0
        fun buildH100x8(): NvlinkTopologyGraph {
            val conn = mutableMapOf<Pair<Int, Int>, Float>()
            for (i in 0..7) for (j in i + 1..7) conn[Pair(i, j)] = 900f / 7f
            return NvlinkTopologyBuilder().build(conn)
        }
    }
}
