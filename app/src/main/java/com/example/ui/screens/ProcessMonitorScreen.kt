package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GpuEntity
import com.example.data.model.ProcessEntity
import com.example.ui.components.StepUpMfaDialog
import com.example.ui.theme.*

@Composable
fun ProcessMonitorScreen(
    gpus: List<GpuEntity>,
    selectedGpuId: String,
    processes: List<ProcessEntity>,
    searchQuery: String,
    userRole: String,
    pendingKillProcess: ProcessEntity?,
    showMfaDialog: Boolean,
    onSelectGpu: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onRequestKill: (ProcessEntity) -> Unit,
    onConfirmKill: (String) -> Unit,
    onCancelKill: () -> Unit
) {
    var selectedGpu = gpus.find { it.id == selectedGpuId } ?: gpus.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "PROCESS & HARDWARE MONITOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberSkyBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "VRAM Allocation & Active Tasks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Surface(
                color = DeepIndigo.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepIndigo)
            ) {
                Text(
                    text = "ROLE: $userRole",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberSkyBlue,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Selected GPU Capability Card
        selectedGpu?.let { gpu ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = gpu.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${gpu.vramTotalMb / 1024} GB VRAM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberSkyBlue,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ECC: ${if (gpu.eccEnabled) "ENABLED" else "DISABLED"}", fontSize = 10.sp, color = TextSecondary)
                        Text("MIG: ${if (gpu.migEnabled) "7 PARTITIONS" else "DISABLED"}", fontSize = 10.sp, color = TextSecondary)
                        Text("BUS: ${gpu.pcieGeneration}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Filter processes by PID, App, or User...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberSkyBlue,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground
            ),
            modifier = Modifier.fillMaxWidth().testTag("process_search_input")
        )

        // Process List Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RUNNING PROCESSES (${processes.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
            Text("VRAM CONSUMED", fontSize = 10.sp, color = TextMuted)
        }

        // Processes List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(processes) { process ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth().testTag("process_item_${process.pid}")
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberSkyBlue, modifier = Modifier.size(18.dp))
                                Text(
                                    text = process.appName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${process.vramUsedMb} MB",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoseError,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = process.command,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "PID: ${process.pid} • User: ${process.user} • CPU: ${process.cpuPct}%",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )

                            Button(
                                onClick = { onRequestKill(process) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoseError.copy(alpha = 0.2f), contentColor = RoseError),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("kill_process_button_${process.pid}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Kill Process", modifier = Modifier.size(12.dp))
                                    Text("Kill Process", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // MFA Step-Up Dialog
    if (showMfaDialog && pendingKillProcess != null) {
        StepUpMfaDialog(
            processName = pendingKillProcess.appName,
            pid = pendingKillProcess.pid,
            onConfirm = onConfirmKill,
            onDismiss = onCancelKill
        )
    }
}
}
