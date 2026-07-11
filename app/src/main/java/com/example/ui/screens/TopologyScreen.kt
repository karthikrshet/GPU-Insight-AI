package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.NvlinkTopologyGraph
import kotlin.math.*

@Composable
fun TopologyScreen(topology: NvlinkTopologyGraph? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("NVLink Topology", style = MaterialTheme.typography.headlineLarge)
        topology?.let {
            Text("${it.gpuCount} GPUs | ${it.totalAggregateBandwidthGbps.toInt()} GB/s aggregate",
                style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            if (topology != null) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val n = topology.gpuCount
                    val cx = size.width / 2; val cy = size.height / 2
                    val r  = minOf(cx, cy) * 0.75f
                    val positions = (0 until n).map { i ->
                        val a = (2 * PI * i / n - PI / 2).toFloat()
                        Offset(cx + r * cos(a), cy + r * sin(a))
                    }
                    topology.edges.forEach { edge ->
                        if (edge.fromGpuId < n && edge.toGpuId < n) {
                            val color = if (edge.isHealthy) Color(0xFF76B900) else Color(0xFFEF4444)
                            drawLine(color, positions[edge.fromGpuId], positions[edge.toGpuId], 2f)
                        }
                    }
                    positions.forEach { drawCircle(Color(0xFF3B82F6), 16f, it) }
                }
            } else {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No topology data", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
