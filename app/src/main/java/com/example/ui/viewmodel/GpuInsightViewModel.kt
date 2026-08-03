package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GpuInsightRepository
import com.example.data.model.*
import com.example.worker.ThermalAlertWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface AiState {
    object Idle : AiState
    object Loading : AiState
    data class Success(val responseText: String) : AiState
    data class Error(val message: String) : AiState
}

class GpuInsightViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GpuInsightRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GpuInsightRepository(database.gpuInsightDao())

        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeDefaultDataIfNeeded()
            ThermalAlertWorker.schedulePeriodicCheck(application)
            startTelemetrySimulation()
        }
    }

    fun triggerImmediateWorkManagerThermalCheck() {
        ThermalAlertWorker.runOneTimeCheck(getApplication())
    }

    // Selected state
    private val _selectedNodeId = MutableStateFlow("node-dgx-h100-01")
    val selectedNodeId: StateFlow<String> = _selectedNodeId.asStateFlow()

    private val _selectedGpuId = MutableStateFlow("gpu-h100-0")
    val selectedGpuId: StateFlow<String> = _selectedGpuId.asStateFlow()

    private val _selectedVendorFilter = MutableStateFlow("ALL")
    val selectedVendorFilter: StateFlow<String> = _selectedVendorFilter.asStateFlow()

    private val _currentUserRole = MutableStateFlow("ADMIN") // OWNER, ADMIN, OPERATOR, VIEWER, AUDITOR
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    private val _processSearchQuery = MutableStateFlow("")
    val processSearchQuery: StateFlow<String> = _processSearchQuery.asStateFlow()

    private val _redactionEnabled = MutableStateFlow(true)
    val redactionEnabled: StateFlow<Boolean> = _redactionEnabled.asStateFlow()

    private val _selectedRetentionWindow = MutableStateFlow("1m")
    val selectedRetentionWindow: StateFlow<String> = _selectedRetentionWindow.asStateFlow()

    // Dialog states
    private val _pendingKillProcess = MutableStateFlow<ProcessEntity?>(null)
    val pendingKillProcess: StateFlow<ProcessEntity?> = _pendingKillProcess.asStateFlow()

    private val _showMfaDialog = MutableStateFlow(false)
    val showMfaDialog: StateFlow<Boolean> = _showMfaDialog.asStateFlow()

    private val _showAddRuleDialog = MutableStateFlow(false)
    val showAddRuleDialog: StateFlow<Boolean> = _showAddRuleDialog.asStateFlow()

    // AI Chat & Advisor State
    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    private val _debugAnalysisResult = MutableStateFlow<String?>(null)
    val debugAnalysisResult: StateFlow<String?> = _debugAnalysisResult.asStateFlow()

    private val _isDebugLoading = MutableStateFlow(false)
    val isDebugLoading: StateFlow<Boolean> = _isDebugLoading.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "Hello! I am GPU Insight AI. I am actively monitoring your GPU cluster across NVIDIA, AMD, and Intel nodes. Ask me anything about performance, VRAM optimization, thermal throttling, or CUDA errors."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Database flows
    val allNodes: StateFlow<List<ClusterNodeEntity>> = repository.allClusterNodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGpus: StateFlow<List<GpuEntity>> = repository.allGpus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredGpus: StateFlow<List<GpuEntity>> = combine(allGpus, selectedNodeId, selectedVendorFilter) { gpus, nodeId, vendor ->
        gpus.filter { gpu ->
            val matchesNode = gpu.nodeId == nodeId || nodeId == "ALL"
            val matchesVendor = vendor == "ALL" || gpu.vendor.equals(vendor, ignoreCase = true)
            matchesNode && matchesVendor
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProcesses: StateFlow<List<ProcessEntity>> = repository.allProcesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProcesses: StateFlow<List<ProcessEntity>> = combine(allProcesses, selectedGpuId, processSearchQuery) { processes, gpuId, query ->
        processes.filter { proc ->
            val matchesGpu = gpuId == "ALL" || proc.gpuId == gpuId
            val matchesQuery = query.isEmpty() ||
                    proc.appName.contains(query, ignoreCase = true) ||
                    proc.user.contains(query, ignoreCase = true) ||
                    proc.pid.toString().contains(query)
            matchesGpu && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedGpuTelemetry: StateFlow<List<MetricTelemetryEntity>> = selectedGpuId.flatMapLatest { id ->
        repository.getLatestTelemetry(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlertRules: StateFlow<List<AlertRuleEntity>> = repository.allAlertRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // State actions
    fun selectNode(nodeId: String) {
        _selectedNodeId.value = nodeId
        val gpusForNode = allGpus.value.filter { it.nodeId == nodeId }
        if (gpusForNode.isNotEmpty()) {
            _selectedGpuId.value = gpusForNode.first().id
        }
    }

    fun selectGpu(gpuId: String) {
        _selectedGpuId.value = gpuId
    }

    fun selectVendorFilter(vendor: String) {
        _selectedVendorFilter.value = vendor
    }

    fun setRole(role: String) {
        _currentUserRole.value = role
        viewModelScope.launch(Dispatchers.IO) {
            repository.logAudit(
                actor = "user@admin.org",
                role = role,
                action = "ROLE_CHANGE",
                resource = "User Session",
                details = "Switched active authorization role to $role",
                ip = "10.0.2.15",
                status = "SUCCESS"
            )
        }
    }

    fun setProcessSearchQuery(query: String) {
        _processSearchQuery.value = query
    }

    fun setRedactionEnabled(enabled: Boolean) {
        _redactionEnabled.value = enabled
        viewModelScope.launch(Dispatchers.IO) {
            repository.logAudit(
                actor = "user@admin.org",
                role = _currentUserRole.value,
                action = "AI_REDACTION",
                resource = "Privacy Pipeline",
                details = "Toggled sensitive telemetry redaction to $enabled",
                ip = "10.0.2.15",
                status = "SUCCESS"
            )
        }
    }

    fun setRetentionWindow(window: String) {
        _selectedRetentionWindow.value = window
    }

    // Process termination
    fun requestKillProcess(process: ProcessEntity) {
        // RBAC check: Only ADMIN, OPERATOR, OWNER can kill processes
        val role = _currentUserRole.value
        if (role != "ADMIN" && role != "OPERATOR" && role != "OWNER") {
            viewModelScope.launch(Dispatchers.IO) {
                repository.logAudit(
                    actor = "user@admin.org",
                    role = role,
                    action = "PROCESS_KILL",
                    resource = "PID ${process.pid}",
                    details = "Permission denied for process termination",
                    ip = "10.0.2.15",
                    status = "DENIED"
                )
            }
            return
        }
        _pendingKillProcess.value = process
        _showMfaDialog.value = true
    }

    fun cancelKillProcess() {
        _pendingKillProcess.value = null
        _showMfaDialog.value = false
    }

    fun confirmKillProcessWithMfa(mfaCode: String) {
        val proc = _pendingKillProcess.value ?: return
        if (mfaCode.length >= 4) { // Simulate MFA check
            viewModelScope.launch(Dispatchers.IO) {
                repository.killProcess(proc.pid, "admin@company.com", _currentUserRole.value, "10.0.2.15")
                _pendingKillProcess.value = null
                _showMfaDialog.value = false
            }
        }
    }

    fun toggleAlertRule(ruleId: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleAlertRule(ruleId, enabled)
        }
    }

    fun setShowAddRuleDialog(show: Boolean) {
        _showAddRuleDialog.value = show
    }

    fun addAlertRule(name: String, metricType: String, threshold: Float, channel: String) {
        val rule = AlertRuleEntity(
            id = "rule-" + System.currentTimeMillis().toString().takeLast(6),
            ruleName = name,
            gpuId = _selectedGpuId.value,
            metricType = metricType,
            thresholdValue = threshold,
            channel = channel,
            enabled = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAlertRule(rule)
            _showAddRuleDialog.value = false
        }
    }

    fun generateExecutiveReport() {
        viewModelScope.launch(Dispatchers.IO) {
            val title = "GPU Insight Executive Audit & Savings Report"
            val text = "Analyzed ${_allGpusCount()} active GPUs across cluster. VRAM efficiency at 89.4%. Transitioning inference workloads to vLLM INT8 quantized mode saved 142.5 kWh daily energy consumption ($280/week cost reduction). Zero thermal throttling violations observed."
            repository.createReport(title, text, 280.0f, 142.5f)
            repository.logAudit(
                actor = "admin@company.com",
                role = _currentUserRole.value,
                action = "REPORT_SHARE",
                resource = "Executive Report",
                details = "Generated new PDF report with expiring tokenized share link",
                ip = "10.0.2.15",
                status = "SUCCESS"
            )
        }
    }

    private fun _allGpusCount(): Int = allGpus.value.size

    // Gemini AI Ask
    fun askAiAdvisor(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage("USER", prompt)
        _chatMessages.value = _chatMessages.value + userMessage
        _aiState.value = AiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val gpu = allGpus.value.find { it.id == _selectedGpuId.value }
            val latestTelemetry = selectedGpuTelemetry.value.firstOrNull()
            val processes = filteredProcesses.value

            val contextText = """
                GPU Name: ${gpu?.name ?: "Unknown GPU"}
                Architecture: ${gpu?.architecture ?: "Hopper"}
                CUDA/Driver: ${gpu?.cudaVersion} / ${gpu?.driverVersion}
                Current Utilization: ${latestTelemetry?.utilizationPct ?: 78f}%
                Current VRAM Used: ${latestTelemetry?.memoryUsedMb ?: 64000} MB / ${gpu?.vramTotalMb ?: 81920} MB
                Current Temp: ${latestTelemetry?.tempC ?: 68}°C
                Power Draw: ${latestTelemetry?.powerW ?: 420f}W / ${gpu?.powerMaxW ?: 700}W
                Tensor Core Saturation: ${latestTelemetry?.tensorCorePct ?: 82f}%
                Inference Tokens/Sec: ${latestTelemetry?.tokensPerSec ?: 124f}
                Running Processes: ${processes.joinToString { "${it.appName} (PID ${it.pid}, VRAM ${it.vramUsedMb}MB)" }}
            """.trimIndent()

            val aiResult = repository.getAiAnalysis(prompt, contextText, _redactionEnabled.value)

            _aiState.value = AiState.Success(aiResult)
            _chatMessages.value = _chatMessages.value + ChatMessage("AI", aiResult)
        }
    }

    fun analyzeDebugLog(logContent: String) {
        if (logContent.isBlank()) return
        _isDebugLoading.value = true
        _debugAnalysisResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val diagnosis = repository.analyzeStackTrace(logContent, _redactionEnabled.value)
            _debugAnalysisResult.value = diagnosis
            _isDebugLoading.value = false
        }
    }

    // Telemetry Simulation (every 2 seconds)
    private suspend fun startTelemetrySimulation() {
        while (true) {
            val gpus = allGpus.value
            for (gpu in gpus) {
                val baseUtil = when {
                    gpu.id.contains("h100") -> 75f + Random.nextFloat() * 20f
                    gpu.id.contains("4090") -> 45f + Random.nextFloat() * 30f
                    else -> 60f + Random.nextFloat() * 25f
                }
                val memoryUsed = (gpu.vramTotalMb * (0.6f + Random.nextFloat() * 0.3f)).toInt()
                val temp = (55 + Random.nextInt(25))
                val power = (gpu.powerMaxW * (0.5f + Random.nextFloat() * 0.45f))
                val fan = 40 + Random.nextInt(45)
                val clock = 1400 + Random.nextInt(600)
                val tensorCore = 50f + Random.nextFloat() * 45f
                val pcie = 12f + Random.nextFloat() * 18f
                val tokens = 80f + Random.nextFloat() * 120f
                val ttft = 12f + Random.nextFloat() * 35f

                val telemetryPoint = MetricTelemetryEntity(
                    gpuId = gpu.id,
                    timestamp = System.currentTimeMillis(),
                    utilizationPct = baseUtil,
                    memoryUsedMb = memoryUsed,
                    tempC = temp,
                    powerW = power,
                    fanSpeedPct = fan,
                    clockGraphicsMhz = clock,
                    clockMemoryMhz = 1200 + Random.nextInt(300),
                    tensorCorePct = tensorCore,
                    pcieThroughputGbps = pcie,
                    tokensPerSec = tokens,
                    ttftMs = ttft
                )
                repository.insertTelemetryPoint(telemetryPoint)
            }
            delay(2000L)
        }
    }
}
