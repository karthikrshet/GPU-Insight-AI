package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class GpuProcess(
    val pid: Int,
    val name: String,
    val vramUsedMb: Long,
    val gpuUtilPercent: Float,
    val gpuId: Int
)

@Composable
fun ProcessMonitorScreen(processes: List<GpuProcess> = emptyList(), modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("GPU Process Monitor", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("${processes.size} active GPU processes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(processes.size) { ProcessRow(processes[it]) }
        }
    }
}

@Composable
private fun ProcessRow(process: GpuProcess) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(process.name, style = MaterialTheme.typography.titleSmall)
                Text("PID: ${process.pid} | GPU #${process.gpuId}",
                    style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${process.vramUsedMb} MB VRAM", style = MaterialTheme.typography.bodySmall)
                Text("${process.gpuUtilPercent.toInt()}% util",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
