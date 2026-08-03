package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetricTelemetryEntity
import com.example.ui.theme.*

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status.uppercase()) {
        "ONLINE" -> StatusOnline to "ONLINE"
        "DEGRADED" -> StatusDegraded to "DEGRADED"
        else -> StatusOffline to "OFFLINE"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun VendorChip(vendor: String) {
    val (color, label) = when (vendor.uppercase()) {
        "NVIDIA" -> Color(0xFF76B900) to "NVIDIA NVML"
        "AMD" -> Color(0xFFED1C24) to "AMD ROCm"
        "INTEL" -> Color(0xFF0071C5) to "Intel XPU"
        else -> CyberSkyBlue to vendor
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TelemetryMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun MetricProgressBar(
    label: String,
    value: Float,
    maxValue: Float,
    unit: String,
    color: Color
) {
    val percentage = (value / maxValue).coerceIn(0f, 1f)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Text(
                text = "${value.toInt()} / ${maxValue.toInt()} $unit (${(percentage * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = CardBorder
        )
    }
}

@Composable
fun MetricSparkline(
    values: List<Float>,
    lineColor: Color = CyberSkyBlue,
    modifier: Modifier = Modifier.width(90.dp).height(32.dp)
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val width = size.width
        val height = size.height
        val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(1f)
        val minVal = (values.minOrNull() ?: 0f).coerceAtMost(maxVal - 1f)
        val range = (maxVal - minVal).coerceAtLeast(1f)

        val stepX = width / (values.size - 1)
        val path = Path()

        values.forEachIndexed { i, v ->
            val x = i * stepX
            val normY = (v - minVal) / range
            val y = height - (normY * height * 0.8f) - (height * 0.1f)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun DynamicRealtimeChart(
    telemetryPoints: List<MetricTelemetryEntity>,
    modifier: Modifier = Modifier.fillMaxWidth().height(180.dp)
) {
    var selectedMetric by remember { mutableStateOf("UTILIZATION") } // UTILIZATION, TEMP, POWER, VRAM
    var timeframe by remember { mutableStateOf("1m") } // 10s, 1m, 5m

    val (metricName, unit, maxScale, chartColor) = when (selectedMetric) {
        "TEMP" -> Quadruple("Temperature", "°C", 100f, RoseError)
        "POWER" -> Quadruple("Power Draw", "W", 500f, AmberWarning)
        "VRAM" -> Quadruple("VRAM Usage", "MB", 80000f, DeepIndigo)
        else -> Quadruple("Compute Utilization", "%", 100f, CyberSkyBlue)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = chartColor, modifier = Modifier.size(16.dp))
                    Text(
                        text = "REAL-TIME TREND: ${metricName.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("UTILIZATION", "TEMP", "POWER", "VRAM").forEach { metric ->
                        val isSelected = selectedMetric == metric
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) chartColor.copy(alpha = 0.25f) else SurfaceSlate)
                                .border(0.5.dp, if (isSelected) chartColor else Color.Transparent, RoundedCornerShape(4.dp))
                                .clickable { selectedMetric = metric }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = metric.take(4),
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) chartColor else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Plotting Area with Grid lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw Horizontal Grid lines (4 divisions)
                val gridColor = CardBorder.copy(alpha = 0.5f)
                for (i in 0..4) {
                    val y = height * (i / 4f)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                if (telemetryPoints.isEmpty()) return@Canvas

                val sampleCount = when (timeframe) {
                    "10s" -> 10
                    "5m" -> 40
                    else -> 25
                }
                val points = telemetryPoints.take(sampleCount).reversed()
                if (points.isEmpty()) return@Canvas

                val stepX = width / (sampleCount - 1).coerceAtLeast(1)
                val path = Path()

                points.forEachIndexed { index, point ->
                    val valFloat = when (selectedMetric) {
                        "TEMP" -> point.tempC.toFloat()
                        "POWER" -> point.powerW
                        "VRAM" -> point.memoryUsedMb.toFloat()
                        else -> point.utilizationPct
                    }

                    val x = index * stepX
                    val normalizedY = (valFloat / maxScale).coerceIn(0f, 1f)
                    val y = height - (normalizedY * height * 0.85f) - (height * 0.05f)

                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                // Draw gradient area
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(chartColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )

                // Draw main stroke line
                drawPath(
                    path = path,
                    color = chartColor,
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun TelemetryWaveformCanvas(
    telemetryPoints: List<MetricTelemetryEntity>,
    lineColor: Color = CyberSkyBlue,
    modifier: Modifier = Modifier.fillMaxWidth().height(100.dp)
) {
    DynamicRealtimeChart(telemetryPoints = telemetryPoints, modifier = modifier.height(180.dp))
}

@Composable
fun StepUpMfaDialog(
    processName: String,
    pid: Int,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Security, contentDescription = null, tint = RoseError)
                Text("Security Step-Up Verification", color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Process termination is a high-privilege action. An audit record will be logged with your identity.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("TARGET PROCESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("$processName (PID: $pid)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                }
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("MFA Authorization Code") },
                    placeholder = { Text("Enter 6-digit TOTP code") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberSkyBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedLabelColor = CyberSkyBlue
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("mfa_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(code) },
                colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                enabled = code.isNotEmpty(),
                modifier = Modifier.testTag("confirm_kill_button")
            ) {
                Text("Verify & Kill Process")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddRuleDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, metricType: String, threshold: Float, channel: String) -> Unit
) {
    var ruleName by remember { mutableStateOf("") }
    var metricType by remember { mutableStateOf("TEMPERATURE") }
    var threshold by remember { mutableStateOf("80") }
    var channel by remember { mutableStateOf("SLACK") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Add Alert & Anomaly Rule", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = ruleName,
                    onValueChange = { ruleName = it },
                    label = { Text("Rule Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = metricType == "TEMPERATURE",
                        onClick = { metricType = "TEMPERATURE" },
                        label = { Text("Temp (°C)") }
                    )
                    FilterChip(
                        selected = metricType == "VRAM",
                        onClick = { metricType = "VRAM" },
                        label = { Text("VRAM (%)") }
                    )
                    FilterChip(
                        selected = metricType == "POWER",
                        onClick = { metricType = "POWER" },
                        label = { Text("Power (W)") }
                    )
                }
                OutlinedTextField(
                    value = threshold,
                    onValueChange = { threshold = it },
                    label = { Text("Threshold Value") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Notification Channel", fontSize = 12.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = channel == "SLACK", onClick = { channel = "SLACK" }, label = { Text("Slack") })
                    FilterChip(selected = channel == "PAGERDUTY", onClick = { channel = "PAGERDUTY" }, label = { Text("PagerDuty") })
                    FilterChip(selected = channel == "WEBHOOK", onClick = { channel = "WEBHOOK" }, label = { Text("Webhook") })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(ruleName.ifEmpty { "New Alert" }, metricType, threshold.toFloatOrNull() ?: 80f, channel) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue)
            ) {
                Text("Create Rule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun NvLinkTopologyMeshCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    Icon(Icons.Default.Hub, contentDescription = null, tint = Color(0xFF76B900), modifier = Modifier.size(18.dp))
                    Text("NVLINK 4.0 FABRIC MATRIX (900 GB/s Interconnect)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Surface(
                    color = Color(0xFF76B900).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("FULL MESH OK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF76B900), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Text("Direct Peer-to-Peer (P2P) NVLink matrix between SXM5 GPU Sockets:", fontSize = 11.sp, color = TextSecondary)

            // Matrix Layout
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceSlate, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Socket", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.width(50.dp))
                    Text("GPU 0", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, modifier = Modifier.weight(1f))
                    Text("GPU 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, modifier = Modifier.weight(1f))
                    Text("GPU 2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, modifier = Modifier.weight(1f))
                    Text("GPU 3", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                listOf(
                    "GPU 0" to listOf("X", "NV9", "NV9", "NV9"),
                    "GPU 1" to listOf("NV9", "X", "NV9", "NV9"),
                    "GPU 2" to listOf("NV9", "NV9", "X", "NV9"),
                    "GPU 3" to listOf("NV9", "NV9", "NV9", "X")
                ).forEach { (gpuLabel, row) ->
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(gpuLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.width(50.dp))
                        row.forEach { cell ->
                            Text(
                                text = cell,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cell == "X") TextMuted else MintGreen,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CudaMemoryProfilerCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Memory, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(18.dp))
                    Text("PYTORCH CUDA MEMORY ALLOCATOR PROFILER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text("CachingAllocator v2", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
            }

            // Stacked Memory Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceSlate)
                ) {
                    Box(modifier = Modifier.weight(0.55f).fillMaxHeight().background(CyberSkyBlue))
                    Box(modifier = Modifier.weight(0.20f).fillMaxHeight().background(ElectricTeal))
                    Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(AmberWarning))
                    Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(CardBorder))
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberSkyBlue))
                        Text("Active Tensors (44 GB)", fontSize = 9.sp, color = TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricTeal))
                        Text("PyTorch Reserved (16 GB)", fontSize = 9.sp, color = TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AmberWarning))
                        Text("Fragmented (8 GB)", fontSize = 9.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun OpenSourceExportDialog(
    onDismiss: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }

    val prometheusMetrics = """
        # HELP gpu_utilization_percent GPU Compute Utilization Percentage
        # TYPE gpu_utilization_percent gauge
        gpu_utilization_percent{gpu="0",name="NVIDIA H100 SXM5",node="cluster-us-east-01"} 78.4
        
        # HELP gpu_temperature_celsius GPU Core Temperature
        # TYPE gpu_temperature_celsius gauge
        gpu_temperature_celsius{gpu="0",name="NVIDIA H100 SXM5",node="cluster-us-east-01"} 68.0
        
        # HELP gpu_power_usage_watts GPU Power Consumption
        # TYPE gpu_power_usage_watts gauge
        gpu_power_usage_watts{gpu="0",name="NVIDIA H100 SXM5",node="cluster-us-east-01"} 420.5
        
        # HELP nvlink_throughput_bytes_per_sec NVLink Bandwidth
        # TYPE nvlink_throughput_bytes_per_sec counter
        nvlink_throughput_bytes_per_sec{gpu="0",link="0"} 900000000000
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Code, contentDescription = null, tint = CyberSkyBlue)
                Text("Prometheus & OpenTelemetry Exporter", color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Export live cluster telemetry to Prometheus, Grafana, or OpenTelemetry collector endpoints:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Surface(
                    color = SurfaceSlate,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = prometheusMetrics,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                if (isCopied) {
                    Text("✓ Metrics endpoint copied to clipboard!", fontSize = 11.sp, color = MintGreen)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { isCopied = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark)
            ) {
                Text(if (isCopied) "Copied!" else "Copy Prometheus Spec")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = TextSecondary) }
        }
    )
}

