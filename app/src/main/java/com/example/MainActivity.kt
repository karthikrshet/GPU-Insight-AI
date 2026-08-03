package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*

enum class NavigationTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "tab_dashboard"),
    PROCESSES("Processes", Icons.Default.Terminal, "tab_processes"),
    AI_ADVISOR("AI Advisor", Icons.Default.AutoAwesome, "tab_ai_advisor"),
    ALERTS("Alerts", Icons.Default.Notifications, "tab_alerts"),
    SECURITY("Security", Icons.Default.Security, "tab_security")
}

class MainActivity : ComponentActivity() {

    private val viewModel: GpuInsightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GpuInsightTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainAppScreen(viewModel: GpuInsightViewModel) {
    var selectedTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    // Observe ViewModel flows
    val nodes by viewModel.allNodes.collectAsStateWithLifecycle()
    val gpus by viewModel.filteredGpus.collectAsStateWithLifecycle()
    val allGpus by viewModel.allGpus.collectAsStateWithLifecycle()
    val selectedNodeId by viewModel.selectedNodeId.collectAsStateWithLifecycle()
    val selectedGpuId by viewModel.selectedGpuId.collectAsStateWithLifecycle()
    val selectedVendor by viewModel.selectedVendorFilter.collectAsStateWithLifecycle()
    val telemetry by viewModel.selectedGpuTelemetry.collectAsStateWithLifecycle()
    val processes by viewModel.filteredProcesses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.processSearchQuery.collectAsStateWithLifecycle()
    val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val redactionEnabled by viewModel.redactionEnabled.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val alertRules by viewModel.allAlertRules.collectAsStateWithLifecycle()
    val auditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()
    val reports by viewModel.allReports.collectAsStateWithLifecycle()
    val pendingKillProcess by viewModel.pendingKillProcess.collectAsStateWithLifecycle()
    val showMfaDialog by viewModel.showMfaDialog.collectAsStateWithLifecycle()
    val showAddRuleDialog by viewModel.showAddRuleDialog.collectAsStateWithLifecycle()
    val debugAnalysisResult by viewModel.debugAnalysisResult.collectAsStateWithLifecycle()
    val isDebugLoading by viewModel.isDebugLoading.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = SurfaceSlate,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "GPU Insight AI",
                                tint = CyberSkyBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "GPU Insight",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationTab.values().forEach { tab ->
                        val selected = selectedTab == tab
                        NavigationRailItem(
                            selected = selected,
                            onClick = { selectedTab = tab },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CyberSkyBlue,
                                selectedTextColor = CyberSkyBlue,
                                indicatorColor = DeepIndigo.copy(alpha = 0.4f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    TabContent(
                        selectedTab = selectedTab,
                        nodes = nodes,
                        gpus = gpus,
                        allGpus = allGpus,
                        selectedNodeId = selectedNodeId,
                        selectedGpuId = selectedGpuId,
                        selectedVendor = selectedVendor,
                        telemetry = telemetry,
                        processes = processes,
                        searchQuery = searchQuery,
                        currentUserRole = currentUserRole,
                        redactionEnabled = redactionEnabled,
                        chatMessages = chatMessages,
                        aiState = aiState,
                        alertRules = alertRules,
                        auditLogs = auditLogs,
                        reports = reports,
                        pendingKillProcess = pendingKillProcess,
                        showMfaDialog = showMfaDialog,
                        showAddRuleDialog = showAddRuleDialog,
                        debugAnalysisResult = debugAnalysisResult,
                        isDebugLoading = isDebugLoading,
                        viewModel = viewModel
                    )
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = SurfaceSlate,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationTab.values().forEach { tab ->
                            val selected = selectedTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { selectedTab = tab },
                                alwaysShowLabel = true,
                                icon = { Icon(tab.icon, contentDescription = tab.title) },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyberSkyBlue,
                                    selectedTextColor = CyberSkyBlue,
                                    indicatorColor = DeepIndigo.copy(alpha = 0.4f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    TabContent(
                        selectedTab = selectedTab,
                        nodes = nodes,
                        gpus = gpus,
                        allGpus = allGpus,
                        selectedNodeId = selectedNodeId,
                        selectedGpuId = selectedGpuId,
                        selectedVendor = selectedVendor,
                        telemetry = telemetry,
                        processes = processes,
                        searchQuery = searchQuery,
                        currentUserRole = currentUserRole,
                        redactionEnabled = redactionEnabled,
                        chatMessages = chatMessages,
                        aiState = aiState,
                        alertRules = alertRules,
                        auditLogs = auditLogs,
                        reports = reports,
                        pendingKillProcess = pendingKillProcess,
                        showMfaDialog = showMfaDialog,
                        showAddRuleDialog = showAddRuleDialog,
                        debugAnalysisResult = debugAnalysisResult,
                        isDebugLoading = isDebugLoading,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TabContent(
    selectedTab: NavigationTab,
    nodes: List<ClusterNodeEntity>,
    gpus: List<GpuEntity>,
    allGpus: List<GpuEntity>,
    selectedNodeId: String,
    selectedGpuId: String,
    selectedVendor: String,
    telemetry: List<MetricTelemetryEntity>,
    processes: List<ProcessEntity>,
    searchQuery: String,
    currentUserRole: String,
    redactionEnabled: Boolean,
    chatMessages: List<ChatMessage>,
    aiState: AiState,
    alertRules: List<AlertRuleEntity>,
    auditLogs: List<AuditLogEntity>,
    reports: List<ReportEntity>,
    pendingKillProcess: ProcessEntity?,
    showMfaDialog: Boolean,
    showAddRuleDialog: Boolean,
    debugAnalysisResult: String?,
    isDebugLoading: Boolean,
    viewModel: GpuInsightViewModel
) {
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
            val targetIndex = targetState.ordinal
            val initialIndex = initialState.ordinal
            if (targetIndex > initialIndex) {
                (slideInHorizontally(animationSpec = tween(300)) { width -> width / 4 } + fadeIn(animationSpec = tween(300)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 4 } + fadeOut(animationSpec = tween(300)))
            } else {
                (slideInHorizontally(animationSpec = tween(300)) { width -> -width / 4 } + fadeIn(animationSpec = tween(300)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width / 4 } + fadeOut(animationSpec = tween(300)))
            }
        },
        label = "TabNavigationTransition",
        modifier = Modifier.fillMaxSize()
    ) { tab ->
        when (tab) {
            NavigationTab.DASHBOARD -> DashboardScreen(
                nodes = nodes,
                gpus = gpus,
                selectedNodeId = selectedNodeId,
                selectedVendor = selectedVendor,
                selectedGpuTelemetry = telemetry,
                onSelectNode = { viewModel.selectNode(it) },
                onSelectVendor = { viewModel.selectVendorFilter(it) },
                onSelectGpu = { viewModel.selectGpu(it) }
            )
            NavigationTab.PROCESSES -> ProcessMonitorScreen(
                gpus = allGpus,
                selectedGpuId = selectedGpuId,
                processes = processes,
                searchQuery = searchQuery,
                userRole = currentUserRole,
                pendingKillProcess = pendingKillProcess,
                showMfaDialog = showMfaDialog,
                onSelectGpu = { viewModel.selectGpu(it) },
                onSearchChange = { viewModel.setProcessSearchQuery(it) },
                onRequestKill = { viewModel.requestKillProcess(it) },
                onConfirmKill = { viewModel.confirmKillProcessWithMfa(it) },
                onCancelKill = { viewModel.cancelKillProcess() }
            )
            NavigationTab.AI_ADVISOR -> AiAdvisorScreen(
                chatMessages = chatMessages,
                aiState = aiState,
                redactionEnabled = redactionEnabled,
                onToggleRedaction = { viewModel.setRedactionEnabled(it) },
                onAskAi = { viewModel.askAiAdvisor(it) },
                debugAnalysisResult = debugAnalysisResult,
                isDebugLoading = isDebugLoading,
                onAnalyzeDebugLog = { viewModel.analyzeDebugLog(it) }
            )
            NavigationTab.ALERTS -> AlertsScreen(
                alertRules = alertRules,
                showAddRuleDialog = showAddRuleDialog,
                onToggleRule = { id, enabled -> viewModel.toggleAlertRule(id, enabled) },
                onShowAddDialog = { viewModel.setShowAddRuleDialog(it) },
                onAddRule = { name, metricType, threshold, channel ->
                    viewModel.addAlertRule(name, metricType, threshold, channel)
                },
                onTriggerWorkManagerCheck = { viewModel.triggerImmediateWorkManagerThermalCheck() }
            )
            NavigationTab.SECURITY -> SecurityReportsScreen(
                currentRole = currentUserRole,
                auditLogs = auditLogs,
                reports = reports,
                onSelectRole = { viewModel.setRole(it) },
                onGenerateReport = { viewModel.generateExecutiveReport() }
            )
        }
    }
}

