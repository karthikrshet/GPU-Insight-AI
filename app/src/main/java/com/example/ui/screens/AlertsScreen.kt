package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertRuleEntity
import com.example.ui.components.AddRuleDialog
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
    alertRules: List<AlertRuleEntity>,
    showAddRuleDialog: Boolean,
    onToggleRule: (String, Boolean) -> Unit,
    onShowAddDialog: (Boolean) -> Unit,
    onAddRule: (name: String, metricType: String, threshold: Float, channel: String) -> Unit,
    onTriggerWorkManagerCheck: () -> Unit = {}
) {
    var chaosModeEnabled by remember { mutableStateOf(false) }
    var workManagerRunMessage by remember { mutableStateOf<String?>(null) }

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
                    text = "ANOMALY DETECTION & ALERTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberSkyBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Automated Thresholds & Channels",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            IconButton(
                onClick = { onShowAddDialog(true) },
                modifier = Modifier
                    .background(CyberSkyBlue, shape = RoundedCornerShape(8.dp))
                    .testTag("add_alert_rule_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Rule", tint = ObsidianDark)
            }
        }

        // WorkManager Background Thermal Monitor Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSkyBlue.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = CyberSkyBlue)
                        Column {
                            Text("WorkManager Background Thermal Daemon", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Periodic 15-min background check active. Saves thermal events to Room DB.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Button(
                        onClick = {
                            onTriggerWorkManagerCheck()
                            workManagerRunMessage = "WorkManager worker executed! Checked GPU thermals & updated Room database."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("trigger_workmanager_button")
                    ) {
                        Text("Run Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                if (workManagerRunMessage != null) {
                    Text(workManagerRunMessage!!, fontSize = 11.sp, color = MintGreen, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Chaos Simulation Toggle Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = AmberWarning)
                    Column {
                        Text("Chaos & Synthetic Spike Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Simulate high thermal loads to test webhook alerts", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Switch(
                    checked = chaosModeEnabled,
                    onCheckedChange = { chaosModeEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AmberWarning, checkedTrackColor = CardBorder),
                    modifier = Modifier.testTag("chaos_mode_switch")
                )
            }
        }

        // Active Anomaly Alert Banner
        if (chaosModeEnabled) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoseError.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseError)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RoseError)
                    Column {
                        Text("ACTIVE ANOMALY DETECTED: Thermal Throttling Risk", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("GPU 0 temperature reached 84°C (Limit: 80°C). Notification dispatched to #gpu-alerts on Slack.", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        }

        // Configured Rules List Header
        Text(
            text = "CONFIGURED ALERT RULES (${alertRules.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )

        // Rules List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(alertRules) { rule ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(14.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(rule.ruleName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = "Metric: ${rule.metricType} • Threshold: > ${rule.thresholdValue.toInt()} • Channel: ${rule.channel}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = rule.enabled,
                            onCheckedChange = { onToggleRule(rule.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberSkyBlue, checkedTrackColor = DeepIndigo)
                        )
                    }
                }
            }
        }
    }

    if (showAddRuleDialog) {
        AddRuleDialog(
            onDismiss = { onShowAddDialog(false) },
            onAdd = onAddRule
        )
    }
}
}
