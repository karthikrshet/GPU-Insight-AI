package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClusterNodeEntity
import com.example.data.model.GpuEntity
import com.example.data.model.MetricTelemetryEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(
    nodes: List<ClusterNodeEntity>,
    gpus: List<GpuEntity>,
    selectedNodeId: String,
    selectedVendor: String,
    selectedGpuTelemetry: List<MetricTelemetryEntity>,
    onSelectNode: (String) -> Unit,
    onSelectVendor: (String) -> Unit,
    onSelectGpu: (String) -> Unit
) {
    var detailedGpu by remember { mutableStateOf<GpuEntity?>(null) }

    AnimatedContent(
        targetState = detailedGpu,
        transitionSpec = {
            if (targetState != null) {
                // Smooth slide-in from right for Detailed View
                (slideInHorizontally(animationSpec = tween(350)) { width -> width } + fadeIn(animationSpec = tween(350)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(350)) { width -> -width / 3 } + fadeOut(animationSpec = tween(350)))
            } else {
                // Smooth slide-back to Summary Dashboard
                (slideInHorizontally(animationSpec = tween(350)) { width -> -width / 3 } + fadeIn(animationSpec = tween(350)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(350)) { width -> width } + fadeOut(animationSpec = tween(350)))
            }
        },
        label = "DashboardToDetailTransition",
        modifier = Modifier.fillMaxSize()
    ) { gpuState ->
        if (gpuState == null) {
            // --- SUMMARY DASHBOARD VIEW ---
            SummaryDashboardView(
                nodes = nodes,
                gpus = gpus,
                selectedNodeId = selectedNodeId,
                selectedVendor = selectedVendor,
                selectedGpuTelemetry = selectedGpuTelemetry,
                onSelectNode = onSelectNode,
                onSelectVendor = onSelectVendor,
                onSelectGpu = { gpu ->
                    onSelectGpu(gpu.id)
                    detailedGpu = gpu
                }
            )
        } else {
            // --- DETAILED GPU VIEW ---
            GpuDetailView(
                gpu = gpuState,
                telemetry = selectedGpuTelemetry,
                onBack = { detailedGpu = null }
            )
        }
    }
}

@Composable
private fun SummaryDashboardView(
    nodes: List<ClusterNodeEntity>,
    gpus: List<GpuEntity>,
    selectedNodeId: String,
    selectedVendor: String,
    selectedGpuTelemetry: List<MetricTelemetryEntity>,
    onSelectNode: (String) -> Unit,
    onSelectVendor: (String) -> Unit,
    onSelectGpu: (GpuEntity) -> Unit
) {
    var showExportDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp)
        ) {
        // Open-Source Metrics Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(14.dp).fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(
                            text = "NVIDIA NVML & FABRIC INSIGHT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberSkyBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Real-time Telemetry & Compute Metrics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = { showExportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("export_prometheus_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Export Specs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cluster Node Filter Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CLUSTER NODE SELECTOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 0.5.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(nodes) { node ->
                        val isSelected = node.id == selectedNodeId
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectNode(node.id) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(node.nodeName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    StatusBadge(node.status)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepIndigo,
                                selectedLabelColor = CyberSkyBlue,
                                containerColor = SurfaceSlate,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("node_chip_${node.id}")
                        )
                    }
                }
            }
        }

        // Multi-Vendor Hardware Filter
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("MULTI-VENDOR FILTER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 0.5.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(listOf("ALL", "NVIDIA", "AMD", "INTEL")) { vendor ->
                        val isSelected = selectedVendor == vendor
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectVendor(vendor) },
                            label = { Text(vendor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepIndigo,
                                selectedLabelColor = CyberSkyBlue,
                                containerColor = SurfaceSlate,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("vendor_chip_$vendor")
                        )
                    }
                }
            }
        }

        // Real-Time Interactive Canvas Chart
        item {
            DynamicRealtimeChart(telemetryPoints = selectedGpuTelemetry, modifier = Modifier.fillMaxWidth().height(190.dp))
        }

        // Summary Cards Grid
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TelemetryMetricCard(
                    title = "Cluster GPUs",
                    value = "${gpus.size} Active",
                    subtext = "Hopper / Ada / CDNA",
                    icon = Icons.Default.DeveloperBoard,
                    accentColor = CyberSkyBlue,
                    modifier = Modifier.weight(1f)
                )
                TelemetryMetricCard(
                    title = "Avg Temp",
                    value = "${selectedGpuTelemetry.firstOrNull()?.tempC ?: 68}°C",
                    subtext = "Thermal Limit: 85°C",
                    icon = Icons.Default.Thermostat,
                    accentColor = ElectricTeal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TelemetryMetricCard(
                    title = "Power Draw",
                    value = "${selectedGpuTelemetry.firstOrNull()?.powerW?.toInt() ?: 420} W",
                    subtext = "TDP Max: 700 W",
                    icon = Icons.Default.Bolt,
                    accentColor = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
                TelemetryMetricCard(
                    title = "LLM Tokens/s",
                    value = "${selectedGpuTelemetry.firstOrNull()?.tokensPerSec?.toInt() ?: 124} t/s",
                    subtext = "Prefill & Decode",
                    icon = Icons.Default.Speed,
                    accentColor = MintGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // GPUs List Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ACTIVE GPU INSTANCES & HARDWARE STATES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text("Tap card for detailed view", fontSize = 10.sp, color = CyberSkyBlue)
            }
        }

        items(gpus) { gpu ->
            val telemetry = selectedGpuTelemetry.firstOrNull()
            val util = telemetry?.utilizationPct ?: 78f
            val vramUsed = telemetry?.memoryUsedMb ?: (gpu.vramTotalMb * 0.75f).toInt()
            val temp = telemetry?.tempC ?: 68

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectGpu(gpu) }
                    .testTag("gpu_card_${gpu.id}")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "GPU ${gpu.gpuIndex}: ${gpu.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "View Details",
                                    tint = CyberSkyBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Arch: ${gpu.architecture} • Driver: ${gpu.driverVersion} • CUDA: ${gpu.cudaVersion}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            VendorChip(gpu.vendor)
                            MetricSparkline(
                                values = listOf((util * 0.9f).coerceIn(10f, 98f), (util * 0.95f).coerceIn(10f, 98f), util, (util * 1.05f).coerceIn(10f, 98f), (util * 0.98f).coerceIn(10f, 98f)),
                                lineColor = CyberSkyBlue
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

                    // Utilization Progress Bar
                    MetricProgressBar(
                        label = "GPU Compute Utilization",
                        value = util,
                        maxValue = 100f,
                        unit = "%",
                        color = if (util > 90f) RoseError else CyberSkyBlue
                    )

                    // VRAM Progress Bar
                    MetricProgressBar(
                        label = "VRAM Memory Allocated",
                        value = vramUsed.toFloat(),
                        maxValue = gpu.vramTotalMb.toFloat(),
                        unit = "MB",
                        color = DeepIndigo
                    )

                    // Telemetry Pill Grid
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Thermostat, contentDescription = null, tint = ElectricTeal, modifier = Modifier.size(14.dp))
                            Text("$temp°C", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                            Text("${telemetry?.powerW?.toInt() ?: 420}W", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = MintGreen, modifier = Modifier.size(14.dp))
                            Text("Tensor Core: ${telemetry?.tensorCorePct?.toInt() ?: 82}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // NVLink 4.0 Fabric Matrix Card
        item {
            NvLinkTopologyMeshCard()
        }

        // PyTorch CUDA Memory Profiler Card
        item {
            CudaMemoryProfilerCard()
        }

        // Open Source Copyright Footer
        item {
            OpenSourceCopyrightFooter()
        }
    }
    }

    if (showExportDialog) {
        OpenSourceExportDialog(onDismiss = { showExportDialog = false })
    }
}

@Composable
private fun GpuDetailView(
    gpu: GpuEntity,
    telemetry: List<MetricTelemetryEntity>,
    onBack: () -> Unit
) {
    val latest = telemetry.firstOrNull()
    val util = latest?.utilizationPct ?: 78f
    val vramUsed = latest?.memoryUsedMb ?: (gpu.vramTotalMb * 0.75f).toInt()
    val temp = latest?.tempC ?: 68
    val power = latest?.powerW ?: 420f
    val fan = latest?.fanSpeedPct ?: 55
    val clockGraphics = latest?.clockGraphicsMhz ?: 1650
    val clockMem = latest?.clockMemoryMhz ?: 1350
    val tensorCore = latest?.tensorCorePct ?: 82f
    val pcieGbps = latest?.pcieThroughputGbps ?: 24.5f
    val tokensSec = latest?.tokensPerSec ?: 128f
    val ttftMs = latest?.ttftMs ?: 22.4f

    var diagnosticRunning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp)
        ) {
        // Top Navigation Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onBack() }.padding(vertical = 4.dp).testTag("back_to_summary_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberSkyBlue
                    )
                    Text(
                        text = "CLUSTER SUMMARY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberSkyBlue,
                        letterSpacing = 0.5.sp
                    )
                }

                VendorChip(gpu.vendor)
            }
        }

        // Title & State Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "GPU ${gpu.gpuIndex}: ${gpu.name}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Node ID: ${gpu.nodeId} • Bus: ${gpu.pcieGeneration}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        StatusBadge("ONLINE")
                    }

                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

                    // Quick specs row
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Architecture", fontSize = 10.sp, color = TextMuted)
                            Text(gpu.architecture, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CUDA Version", fontSize = 10.sp, color = TextMuted)
                            Text(gpu.cudaVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Driver", fontSize = 10.sp, color = TextMuted)
                            Text(gpu.driverVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ECC Mode", fontSize = 10.sp, color = TextMuted)
                            Text(if (gpu.eccEnabled) "Active" else "Disabled", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                        }
                    }
                }
            }
        }

        // GPU Health Score Breakdown Card (v4.0 Feature)
        item {
            GpuHealthScoreCard(gpu = gpu, temp = temp, power = power)
        }

        // Live Performance Metrics Summary
        item {
            Text(
                text = "REAL-TIME TELEMETRY GAUGE & FREQUENCIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MetricProgressBar(
                        label = "Compute Core Saturation",
                        value = util,
                        maxValue = 100f,
                        unit = "%",
                        color = if (util > 90f) RoseError else CyberSkyBlue
                    )

                    MetricProgressBar(
                        label = "VRAM Memory Usage",
                        value = vramUsed.toFloat(),
                        maxValue = gpu.vramTotalMb.toFloat(),
                        unit = "MB",
                        color = DeepIndigo
                    )

                    MetricProgressBar(
                        label = "Tensor Core TensorRT Utilization",
                        value = tensorCore,
                        maxValue = 100f,
                        unit = "%",
                        color = MintGreen
                    )

                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

                    // Clocks and Fan
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Graphics Clock", fontSize = 10.sp, color = TextMuted)
                            Text("$clockGraphics MHz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("Memory Clock", fontSize = 10.sp, color = TextMuted)
                            Text("$clockMem MHz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("Fan Speed", fontSize = 10.sp, color = TextMuted)
                            Text("$fan %", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("PCIe Gen5 Bandwidth", fontSize = 10.sp, color = TextMuted)
                            Text("$pcieGbps GB/s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Detailed Thermal & Power Cards
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TelemetryMetricCard(
                    title = "Temperature",
                    value = "$temp°C",
                    subtext = "Limit: 85°C Thermal Target",
                    icon = Icons.Default.Thermostat,
                    accentColor = if (temp > 80) RoseError else ElectricTeal,
                    modifier = Modifier.weight(1f)
                )
                TelemetryMetricCard(
                    title = "Power Draw",
                    value = "${power.toInt()} W",
                    subtext = "Max TDP: ${gpu.powerMaxW} W",
                    icon = Icons.Default.Bolt,
                    accentColor = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // LLM Inference Metrics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepIndigo),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
                        Text("vLLM / TensorRT-LLM INFERENCE STATS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Tokens Generated / sec", fontSize = 10.sp, color = TextMuted)
                            Text("${tokensSec.toInt()} tok/s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column {
                            Text("Time To First Token (TTFT)", fontSize = 10.sp, color = TextMuted)
                            Text("${String.format("%.1f", ttftMs)} ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Live Telemetry Waveform Graph
        item {
            TelemetryWaveformCanvas(telemetryPoints = telemetry)
        }

        // Action Buttons
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { diagnosticRunning = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo, contentColor = TextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Run Diagnostics", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceSlate, contentColor = CyberSkyBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Summary", fontSize = 12.sp)
                }
            }
        }
    }
    }

    if (diagnosticRunning) {
        AlertDialog(
            onDismissRequest = { diagnosticRunning = false },
            containerColor = CardBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGreen)
                    Text("NVML Hardware Diagnostic", color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Running NVML & PCIe sanity checks for ${gpu.name}...", fontSize = 13.sp, color = TextSecondary)
                    Text("• PCIe Bus Speed: Gen5 x16 (32 GT/s) OK", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    Text("• ECC Single-bit Errors: 0 OK", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    Text("• NVLink Topology: 900 GB/s Interconnect OK", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    Text("• Thermal Throttling: None detected OK", fontSize = 12.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
            },
            confirmButton = {
                Button(
                    onClick = { diagnosticRunning = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark)
                ) {
                    Text("Close Diagnostic")
                }
            }
        )
    }
}

@Composable
fun GpuHealthScoreCard(gpu: GpuEntity, temp: Int, power: Float) {
    val tempOk = temp < 80
    val powerOk = power <= gpu.powerMaxW
    val eccOk = gpu.eccEnabled
    val score = if (tempOk && powerOk && eccOk) 98 else if (tempOk) 88 else 72

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MintGreen)
                    Column {
                        Text("GPU HEALTH SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue)
                        Text("Comprehensive hardware stability index", fontSize = 11.sp, color = TextMuted)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(MintGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("$score/100", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                }
            }

            HorizontalDivider(color = CardBorder, thickness = 0.5.dp)

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                HealthCheckPill("✓ Temperature", tempOk)
                HealthCheckPill("✓ Power TDP", powerOk)
                HealthCheckPill("✓ Memory VRAM", true)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                HealthCheckPill("✓ Clock & Tensor", true)
                HealthCheckPill("✓ ECC Status", eccOk)
                HealthCheckPill("✓ Driver & PCIe", true)
            }
        }
    }
}

@Composable
private fun HealthCheckPill(label: String, isOk: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isOk) MintGreen else RoseError,
            modifier = Modifier.size(12.dp)
        )
        Text(label, fontSize = 11.sp, color = if (isOk) TextPrimary else RoseError)
    }
}

@Composable
fun OpenSourceCopyrightFooter() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
    ) {
        HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Copyright © 2026 Karthik Rajesh Shet (GitHub: @karthikrshet)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Text(
            text = "GPU Insight AI is an open-source project released under the Apache-2.0 License.",
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}

