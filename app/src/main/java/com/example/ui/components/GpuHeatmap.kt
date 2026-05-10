package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
fun GpuHeatmap(
    gpuTemperatures: Map<Int, Float>,
    columns: Int = 4,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Temperature Heatmap", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val n = gpuTemperatures.size.coerceAtLeast(1)
            val rows = (n + columns - 1) / columns
            val cellW = size.width / columns
            val cellH = size.height / rows
            gpuTemperatures.entries.forEachIndexed { idx, (_, temp) ->
                val col = idx % columns; val row = idx / columns
                val t = ((temp - 30f) / 70f).coerceIn(0f, 1f)
                drawRect(lerp(Color(0xFF16A34A), Color(0xFFEF4444), t),
                    Offset(col * cellW + 2f, row * cellH + 2f), Size(cellW - 4f, cellH - 4f))
            }
        }
    }
}
