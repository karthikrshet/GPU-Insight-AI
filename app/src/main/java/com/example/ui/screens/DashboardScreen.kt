package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.GpuMetric
import com.example.ui.viewmodel.GpuInsightUiState

@Composable
fun DashboardScreen(uiState: GpuInsightUiState, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") {
            Column {
                Text("GPU Cluster Dashboard", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(4.dp))
                Text("${uiState.metrics.size} GPUs monitored",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
        items(uiState.metrics, key = { it.id }) { metric ->
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                GpuMetricCard(metric = metric)
            }
        }
    }
}

@Composable
private fun GpuMetricCard(metric: GpuMetric) {
    val vramProgress = if (metric.vramTotalMb > 0) {
        (metric.vramUsedMb.toFloat() / metric.vramTotalMb).coerceIn(0f, 1f)
    } else 0f

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(metric.gpuName, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("${metric.utilizationPercent.toInt()}% Util") })
                AssistChip(onClick = {}, label = { Text("${metric.temperatureCelsius.toInt()}°C") })
                AssistChip(onClick = {}, label = { Text("${metric.powerDrawWatts.toInt()}W") })
                AssistChip(onClick = {}, label = { Text("${metric.clockFrequencyMhz} MHz") })
            }
            LinearProgressIndicator(progress = { vramProgress }, modifier = Modifier.fillMaxWidth())
            Text("VRAM: ${metric.vramUsedMb} / ${metric.vramTotalMb} MB",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
