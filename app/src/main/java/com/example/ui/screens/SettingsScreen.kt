package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    thermalThreshold: Float = 85f,
    refreshIntervalMs: Long = 500L,
    onThermalChange: (Float) -> Unit = {},
    onIntervalChange: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Thermal Alert Threshold: ${thermalThreshold.toInt()}°C",
                    style = MaterialTheme.typography.titleMedium)
                Slider(value = thermalThreshold, onValueChange = onThermalChange,
                    valueRange = 60f..100f, steps = 39)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Refresh Rate", style = MaterialTheme.typography.titleMedium)
                listOf(250L to "250ms", 500L to "500ms", 1000L to "1s", 2000L to "2s").forEach { (ms, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(refreshIntervalMs == ms, { onIntervalChange(ms) })
                        Spacer(Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        }
    }
}
