package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.BenchmarkResult

@Composable
fun BenchmarkScreen(
    results: List<BenchmarkResult> = emptyList(),
    onRunBenchmark: () -> Unit = {},
    isRunning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("GPU Benchmarks", style = MaterialTheme.typography.headlineLarge)
            Button(onClick = onRunBenchmark, enabled = !isRunning) {
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (isRunning) "Running..." else "Run Benchmark")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (results.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Run a benchmark to compare GPU performance",
                    modifier = Modifier.padding(24.dp))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results.size) { i ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()) {
                                Text("#${i+1} ${results[i].gpuName}",
                                    style = MaterialTheme.typography.titleMedium)
                                Text("Score: ${results[i].score}", color = Color(0xFF76B900),
                                    style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("${"%.0f".format(results[i].peakTflops)} TFLOPS | " +
                                 "${results[i].memoryBandwidthGbps.toInt()} GB/s BW",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
