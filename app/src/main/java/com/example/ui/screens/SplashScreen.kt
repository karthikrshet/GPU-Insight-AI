package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    val alpha by animateFloatAsState(1f,
        tween(1200, easing = EaseInOutCubic), label = "alpha")
    val scale by animateFloatAsState(1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "scale")

    LaunchedEffect(Unit) { delay(2500); onComplete() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0E1A), Color(0xFF1E293B)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚡", fontSize = 64.sp, modifier = Modifier.scale(scale).alpha(alpha))
            Spacer(Modifier.height(16.dp))
            Text("GPU Insight AI", fontSize = 32.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF76B900), modifier = Modifier.alpha(alpha))
            Spacer(Modifier.height(8.dp))
            Text("AI-Powered GPU Infrastructure Platform",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.alpha(alpha))
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = Color(0xFF76B900),
                modifier = Modifier.size(32.dp).alpha(alpha))
        }
    }
}
