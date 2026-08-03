package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.AuditLogEntity
import com.example.data.model.ReportEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SecurityReportsScreen(
    currentRole: String,
    auditLogs: List<AuditLogEntity>,
    reports: List<ReportEntity>,
    onSelectRole: (String) -> Unit,
    onGenerateReport: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

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
        // Header
        item {
            Column {
                Text(
                    text = "SECURITY, AUDIT LOG & COMPLIANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberSkyBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "RBAC Roles & Executive Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Role Switcher Card
        item {
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
                        Text("ACTIVE MULTI-TENANT RBAC ROLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(currentRole, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listOf("OWNER", "ADMIN", "OPERATOR", "VIEWER", "AUDITOR")) { role ->
                            val isSel = currentRole == role
                            FilterChip(
                                selected = isSel,
                                onClick = { onSelectRole(role) },
                                label = { Text(role, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepIndigo,
                                    selectedLabelColor = CyberSkyBlue,
                                    containerColor = SurfaceSlate,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Cost & Carbon Tracking Card
        item {
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = MintGreen)
                        Text("COST & CARBON FOOTPRINT ESTIMATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cloud Cost Rate", fontSize = 10.sp, color = TextMuted)
                            Text("$2.85 / GPU-hr", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Est. Energy", fontSize = 10.sp, color = TextMuted)
                            Text("142.5 kWh / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Carbon Intensity", fontSize = 10.sp, color = TextMuted)
                            Text("85.4 kg CO2e", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Button(
                        onClick = onGenerateReport,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("generate_report_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Generate 1-Click Executive PDF Report",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Generated Reports List
        if (reports.isNotEmpty()) {
            item {
                Text("GENERATED EXECUTIVE REPORTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }
            items(reports) { rep ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(rep.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(rep.summaryText, fontSize = 12.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Share Token: ${rep.shareToken.take(12)}...", fontSize = 10.sp, color = CyberSkyBlue, fontFamily = FontFamily.Monospace)
                            Text("Savings: $${rep.costSavingsEst.toInt()}/wk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                        }
                    }
                }
            }
        }

        // Immutable Audit Log Header
        item {
            Text("IMMUTABLE AUDIT LOG (LAST 100 EVENTS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        }

        items(auditLogs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberSkyBlue)
                            Text("by ${log.actor} (${log.role})", fontSize = 11.sp, color = TextPrimary)
                        }
                        Text(log.details, fontSize = 11.sp, color = TextSecondary)
                        Text("${dateFormat.format(Date(log.timestamp))} • IP: ${log.ipAddress}", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                    }

                    Surface(
                        color = if (log.status == "SUCCESS") MintGreen.copy(alpha = 0.2f) else RoseError.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = log.status,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.status == "SUCCESS") MintGreen else RoseError,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
    }
}
