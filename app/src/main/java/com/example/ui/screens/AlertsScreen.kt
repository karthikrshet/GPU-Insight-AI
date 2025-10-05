package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class GpuAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val timestamp: Long
)

enum class AlertSeverity { INFO, WARNING, CRITICAL }

@Composable
fun AlertsScreen(alerts: List<GpuAlert> = emptyList(), modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Thermal & Performance Alerts", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        if (alerts.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("No active alerts — all GPUs healthy ✓",
                    modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(alerts.size) { AlertCard(alerts[it]) }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: GpuAlert) {
    val containerColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> Color(0xFF7F1D1D)
        AlertSeverity.WARNING  -> Color(0xFF78350F)
        AlertSeverity.INFO     -> Color(0xFF1E3A5F)
    }
    Card(colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Warning, null, tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(alert.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(alert.description, style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}
