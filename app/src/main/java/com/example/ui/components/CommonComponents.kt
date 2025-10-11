package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GpuGaugeChart(
    value: Float,
    label: String,
    color: Color = Color(0xFF76B900),
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic),
        label = "gauge"
    )
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.toPx() * 0.12f
            drawArc(Color.Gray.copy(alpha = 0.3f), 135f, 270f, false,
                style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(color, 135f, 270f * animatedValue, false,
                style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun LoadingShimmer(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        0.3f, 0.9f,
        infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmer_alpha"
    )
    Card(modifier = modifier.fillMaxWidth().height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = alpha))) {}
}

@Composable
fun StatusBadge(text: String, isHealthy: Boolean) {
    val color = if (isHealthy) Color(0xFF16A34A) else Color(0xFFDC2626)
    Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall, color = color)
    }
}
