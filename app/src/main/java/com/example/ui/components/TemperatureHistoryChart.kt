package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun TemperatureHistoryChart(
    temperatures: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFF59E0B),
    criticalTemp: Float = 90f
) {
    Column(modifier = modifier) {
        Text("Temperature History (°C)", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            if (temperatures.size < 2) return@Canvas
            val min = temperatures.minOrNull() ?: 0f
            val max = (temperatures.maxOrNull() ?: 100f).coerceAtLeast(min + 1f)
            val w = size.width / (temperatures.size - 1)
            val critY = size.height - (criticalTemp - min) / (max - min) * size.height
            drawLine(Color(0xFFEF4444).copy(alpha = 0.5f),
                Offset(0f, critY), Offset(size.width, critY), strokeWidth = 1f)
            val path = Path()
            temperatures.forEachIndexed { i, temp ->
                val x = i * w
                val y = size.height - (temp - min) / (max - min) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, lineColor, style = Stroke(width = 2f))
        }
    }
}
