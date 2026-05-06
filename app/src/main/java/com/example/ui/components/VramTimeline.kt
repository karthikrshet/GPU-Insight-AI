package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VramUsageTimeline(
    usagePercents: List<Float>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF3B82F6)
) {
    Column(modifier = modifier) {
        Text("VRAM Usage History", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
            val barW = size.width / usagePercents.size.coerceAtLeast(1)
            usagePercents.forEachIndexed { i, pct ->
                val barH = size.height * (pct / 100f).coerceIn(0f, 1f)
                drawRect(barColor.copy(alpha = 0.5f + 0.5f * (pct / 100f)),
                    Offset(i * barW, size.height - barH), Size(barW - 1f, barH))
            }
        }
    }
}
