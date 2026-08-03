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
import com.example.ui.viewmodel.AiState
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.theme.*

@Composable
fun AiAdvisorScreen(
    chatMessages: List<ChatMessage>,
    aiState: AiState,
    redactionEnabled: Boolean,
    onToggleRedaction: (Boolean) -> Unit,
    onAskAi: (String) -> Unit,
    debugAnalysisResult: String? = null,
    isDebugLoading: Boolean = false,
    onAnalyzeDebugLog: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Chat Advisor, 1: AI Debug Assistant
    var inputText by remember { mutableStateOf("") }
    var debugLogText by remember { mutableStateOf("") }

    val presetPrompts = listOf(
        "Why is GPU 0 at low utilization?",
        "Explain CUDA OOM Error cause",
        "Optimize vLLM batch size & KV cache",
        "Suggest TensorRT FP16 quantization",
        "Compare H100 vs MI300X throughput"
    )

    val sampleDebugLogs = listOf(
        "CUDA OOM" to "torch.OutOfMemoryError: CUDA out of memory. Tried to allocate 12.50 GiB (GPU 0; 80.00 GiB total capacity; 68.20 GiB allocated; 1.80 GiB free; 74.00 GiB reserved by PyTorch).",
        "Illegal Memory" to "RuntimeError: CUDA error: an illegal memory access was encountered. CUDA kernel errors might be asynchronously reported at some other API call. Set CUDA_LAUNCH_BLOCKING=1.",
        "NCCL Timeout" to "Watchdog caught collective timeout error : WorkNCCL(SeqNum=14205, OpType=ALLREDUCE, Timeout(ms)=600000) ran for 600005 milliseconds before timing out. Ring send failed."
    )

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberSkyBlue)
                    Text(
                        text = "GEMINI AI SUITE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberSkyBlue,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = if (selectedTab == 0) "Cluster Advisor & Telemetry QA" else "AI Debug Assistant & Log Diagnostic",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Sub-Navigation Segmented Buttons
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceSlate,
            contentColor = CyberSkyBlue,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Chat Advisor", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_chat_advisor")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Debug Assistant", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_debug_assistant")
            )
        }

        // Privacy Redaction Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DeepIndigo)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MintGreen, modifier = Modifier.size(16.dp))
                    Text("Privacy Redaction Pipeline (Strips IPs & path usernames)", fontSize = 11.sp, color = TextSecondary)
                }
                Switch(
                    checked = redactionEnabled,
                    onCheckedChange = onToggleRedaction,
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberSkyBlue, checkedTrackColor = DeepIndigo),
                    modifier = Modifier.testTag("redaction_switch")
                )
            }
        }

        if (selectedTab == 0) {
            // --- CHAT ADVISOR TAB ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presetPrompts) { prompt ->
                    SuggestionChip(
                        onClick = { onAskAi(prompt) },
                        label = { Text(prompt, fontSize = 11.sp, color = TextPrimary) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = CardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(chatMessages) { msg ->
                    val isUser = msg.sender == "USER"
                    Row(
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) DeepIndigo else CardBackground
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUser) 12.dp else 2.dp,
                                bottomEnd = if (isUser) 2.dp else 12.dp
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) CyberSkyBlue else CardBorder),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isUser) Icons.Default.Person else Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isUser) TextPrimary else CyberSkyBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isUser) "You" else "GPU Insight AI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) TextPrimary else CyberSkyBlue
                                    )
                                }
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    fontFamily = if (!isUser) FontFamily.Monospace else FontFamily.Default
                                )
                            }
                        }
                    }
                }

                if (aiState is AiState.Loading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberSkyBlue, strokeWidth = 2.dp)
                            Text("Gemini AI is analyzing hardware telemetry...", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Gemini about GPU telemetry or CUDA...", fontSize = 12.sp, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberSkyBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    modifier = Modifier.weight(1f).testTag("ai_prompt_input")
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onAskAi(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .background(CyberSkyBlue, shape = RoundedCornerShape(8.dp))
                        .testTag("send_ai_prompt_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = ObsidianDark)
                }
            }
        } else {
            // --- AI DEBUG ASSISTANT TAB ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Paste CUDA errors, PyTorch stack traces, TensorFlow logs, or NCCL failure traces for instant Gemini AI root cause analysis.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                item {
                    Text("Preset Sample Logs:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        items(sampleDebugLogs) { (title, log) ->
                            FilterChip(
                                selected = debugLogText == log,
                                onClick = { debugLogText = log },
                                label = { Text(title, fontSize = 11.sp) },
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

                item {
                    OutlinedTextField(
                        value = debugLogText,
                        onValueChange = { debugLogText = it },
                        placeholder = { Text("Paste CUDA stack trace, PyTorch error, or NCCL timeout log here...", fontSize = 12.sp, color = TextMuted) },
                        minLines = 4,
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberSkyBlue,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("debug_stack_trace_input")
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (debugLogText.isNotBlank()) {
                                onAnalyzeDebugLog(debugLogText)
                            }
                        },
                        enabled = !isDebugLoading && debugLogText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSkyBlue, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("analyze_stack_trace_button")
                    ) {
                        if (isDebugLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = ObsidianDark, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Log with Gemini AI...", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Analyze Root Cause & Solution", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                if (debugAnalysisResult != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
                                    Text("Gemini AI Diagnostic Report", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintGreen)
                                }
                                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                                Text(
                                    text = debugAnalysisResult,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
