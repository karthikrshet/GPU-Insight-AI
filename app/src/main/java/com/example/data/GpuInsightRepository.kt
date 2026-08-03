package com.example.data

import com.example.data.dao.GpuInsightDao
import com.example.data.model.*
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class GpuInsightRepository(private val dao: GpuInsightDao) {

    val allClusterNodes: Flow<List<ClusterNodeEntity>> = dao.getAllClusterNodes()
    val allGpus: Flow<List<GpuEntity>> = dao.getAllGpus()
    val allProcesses: Flow<List<ProcessEntity>> = dao.getAllProcesses()
    val allAlertRules: Flow<List<AlertRuleEntity>> = dao.getAllAlertRules()
    val allAuditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    val allReports: Flow<List<ReportEntity>> = dao.getAllReports()

    fun getGpusForNode(nodeId: String): Flow<List<GpuEntity>> = dao.getGpusForNode(nodeId)
    fun getProcessesForGpu(gpuId: String): Flow<List<ProcessEntity>> = dao.getProcessesForGpu(gpuId)
    fun getLatestTelemetry(gpuId: String): Flow<List<MetricTelemetryEntity>> = dao.getLatestTelemetryForGpu(gpuId)

    suspend fun initializeDefaultDataIfNeeded() {
        val currentNodes = allClusterNodes.first()
        if (currentNodes.isEmpty()) {
            val initialNodes = listOf(
                ClusterNodeEntity(
                    id = "node-dgx-h100-01",
                    nodeName = "dgx-h100-alpha.cluster.local",
                    ipAddress = "10.240.0.11",
                    vendor = "NVIDIA",
                    status = "ONLINE",
                    gpuCount = 8,
                    region = "us-west-1a",
                    isPrimary = true
                ),
                ClusterNodeEntity(
                    id = "node-rtx-4090-02",
                    nodeName = "ai-lab-rtx4090.research.org",
                    ipAddress = "192.168.1.105",
                    vendor = "NVIDIA",
                    status = "ONLINE",
                    gpuCount = 4,
                    region = "on-prem-datacenter",
                    isPrimary = false
                ),
                ClusterNodeEntity(
                    id = "node-mi300x-03",
                    nodeName = "rocm-mi300x-cluster-01",
                    ipAddress = "10.240.2.88",
                    vendor = "AMD",
                    status = "DEGRADED",
                    gpuCount = 4,
                    region = "us-central1-b",
                    isPrimary = false
                ),
                ClusterNodeEntity(
                    id = "node-intel-xpu-04",
                    nodeName = "intel-max1100-node",
                    ipAddress = "10.240.4.19",
                    vendor = "INTEL",
                    status = "ONLINE",
                    gpuCount = 2,
                    region = "europe-west3-a",
                    isPrimary = false
                )
            )
            dao.insertClusterNodes(initialNodes)

            val initialGpus = listOf(
                // Node 1: DGX H100
                GpuEntity("gpu-h100-0", "node-dgx-h100-01", 0, "NVIDIA H100 SXM5 80GB", "NVIDIA", "Hopper", "535.129.03", "12.2", 81920, 700, 85, eccEnabled = true, migEnabled = true),
                GpuEntity("gpu-h100-1", "node-dgx-h100-01", 1, "NVIDIA H100 SXM5 80GB", "NVIDIA", "Hopper", "535.129.03", "12.2", 81920, 700, 85, eccEnabled = true, migEnabled = false),
                GpuEntity("gpu-h100-2", "node-dgx-h100-01", 2, "NVIDIA H100 SXM5 80GB", "NVIDIA", "Hopper", "535.129.03", "12.2", 81920, 700, 85, eccEnabled = true, migEnabled = false),
                GpuEntity("gpu-h100-3", "node-dgx-h100-01", 3, "NVIDIA H100 SXM5 80GB", "NVIDIA", "Hopper", "535.129.03", "12.2", 81920, 700, 85, eccEnabled = true, migEnabled = false),
                
                // Node 2: RTX 4090 Cluster
                GpuEntity("gpu-4090-0", "node-rtx-4090-02", 0, "NVIDIA GeForce RTX 4090", "NVIDIA", "Ada Lovelace", "545.29.06", "12.3", 24576, 450, 80, eccEnabled = false, migEnabled = false),
                GpuEntity("gpu-4090-1", "node-rtx-4090-02", 1, "NVIDIA GeForce RTX 4090", "NVIDIA", "Ada Lovelace", "545.29.06", "12.3", 24576, 450, 80, eccEnabled = false, migEnabled = false),

                // Node 3: AMD Instinct MI300X
                GpuEntity("gpu-mi300x-0", "node-mi300x-03", 0, "AMD Instinct MI300X 192GB", "AMD", "CDNA 3", "ROCm 6.1.2", "N/A", 196608, 750, 90, eccEnabled = true, migEnabled = false),
                GpuEntity("gpu-mi300x-1", "node-mi300x-03", 1, "AMD Instinct MI300X 192GB", "AMD", "CDNA 3", "ROCm 6.1.2", "N/A", 196608, 750, 90, eccEnabled = true, migEnabled = false),

                // Node 4: Intel Max 1100
                GpuEntity("gpu-intel-0", "node-intel-xpu-04", 0, "Intel Data Center GPU Max 1100", "INTEL", "Xe-HPC", "oneAPI 2024.1", "N/A", 49152, 300, 80, eccEnabled = true, migEnabled = false)
            )
            dao.insertGpus(initialGpus)

            val initialProcesses = listOf(
                ProcessEntity(1024, "gpu-h100-0", "ml-engineer", "vllm-serve-llama3-70b", "python3 -m vllm.entrypoints.openai.api_server --model meta-llama/Meta-Llama-3-70B --tensor-parallel-size 2", 68500, 88.5f, 14200L),
                ProcessEntity(2048, "gpu-h100-1", "researcher-alex", "deepspeed-train-qwen2", "deepspeed --num_gpus=2 train_qwen2_72b.py --stage 3", 72100, 95.2f, 8900L),
                ProcessEntity(3096, "gpu-4090-0", "dev-user", "comfyui-flux1-dev", "python main.py --use-pytorch-cross-attention --highvram", 19800, 64.0f, 3200L),
                ProcessEntity(4112, "gpu-4090-1", "student-sam", "llama.cpp-server", "./server -m models/7B/ggml-model-q4_0.gguf -c 4096 -gld 99", 8400, 32.1f, 1800L),
                ProcessEntity(5010, "gpu-mi300x-0", "rocm-user", "vllm-rocm-mi300x", "python3 -m vllm.entrypoints.openai.api_server --model mistralai/Mixtral-8x22B", 142000, 91.0f, 21000L),
                ProcessEntity(6022, "gpu-intel-0", "oneapi-dev", "torch-xpu-resnet50", "python resnet_benchmark.py --device xpu", 18500, 42.0f, 950L)
            )
            dao.insertProcesses(initialProcesses)

            val initialAlertRules = listOf(
                AlertRuleEntity("rule-01", "Thermal Throttling Alert (>80°C)", "gpu-h100-0", "TEMPERATURE", 80.0f, "SLACK", enabled = true),
                AlertRuleEntity("rule-02", "Predictive OOM Memory Warning (>90%)", "gpu-h100-0", "VRAM", 90.0f, "PAGERDUTY", enabled = true),
                AlertRuleEntity("rule-03", "Power Draw TDP Cap Exceeded (>650W)", "gpu-h100-0", "POWER", 650.0f, "DISCORD", enabled = true),
                AlertRuleEntity("rule-04", "ECC Uncorrectable Error Detector", "gpu-h100-0", "ECC", 1.0f, "WEBHOOK", enabled = true),
                AlertRuleEntity("rule-05", "Anomaly Anomaly Drift Detector", "gpu-mi300x-0", "ANOMALY", 85.0f, "EMAIL", enabled = true)
            )
            dao.insertAlertRules(initialAlertRules)

            val initialAuditLogs = listOf(
                AuditLogEntity(timestamp = System.currentTimeMillis() - 3600000L, actor = "admin@company.com", role = "ADMIN", action = "LOGIN", targetResource = "Dashboard", details = "User logged in with OIDC SSO", ipAddress = "10.0.4.12", status = "SUCCESS"),
                AuditLogEntity(timestamp = System.currentTimeMillis() - 1800000L, actor = "operator-john", role = "OPERATOR", action = "PROCESS_KILL", targetResource = "PID 8912 (zombie-train)", details = "Terminated stuck PyTorch DDP trainer process", ipAddress = "10.0.4.15", status = "SUCCESS"),
                AuditLogEntity(timestamp = System.currentTimeMillis() - 900000L, actor = "system-ai", role = "SYSTEM", action = "AI_REDACTION", targetResource = "Gemini AI Advisor", details = "Redacted 3 confidential hostnames and IPs before LLM submission", ipAddress = "127.0.0.1", status = "SUCCESS"),
                AuditLogEntity(timestamp = System.currentTimeMillis() - 300000L, actor = "auditor-sarah", role = "AUDITOR", action = "REPORT_SHARE", targetResource = "Executive GPU Summary v1", details = "Generated expiring public share link (7 days)", ipAddress = "10.0.4.88", status = "SUCCESS")
            )
            for (log in initialAuditLogs) {
                dao.insertAuditLog(log)
            }

            val initialReports = listOf(
                ReportEntity(
                    id = "rep-" + UUID.randomUUID().toString().take(8),
                    title = "Weekly Cluster Telemetry & Efficiency Report",
                    generatedAt = System.currentTimeMillis() - 86400000L,
                    summaryText = "Cluster operating at 84.2% average GPU utilization. Identified 12.4% idle VRAM waste on RTX 4090 nodes. Transitioning vLLM workers to INT8 quantization saved $420/week in power and cloud compute costs.",
                    shareToken = UUID.randomUUID().toString(),
                    costSavingsEst = 420.0f,
                    carbonKg = 185.4f
                )
            )
            for (rep in initialReports) {
                dao.insertReport(rep)
            }
        }
    }

    suspend fun insertTelemetryPoint(telemetry: MetricTelemetryEntity) {
        dao.insertTelemetry(telemetry)
    }

    suspend fun killProcess(pid: Int, actor: String, role: String, userIp: String): Boolean {
        return try {
            dao.deleteProcess(pid)
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = System.currentTimeMillis(),
                    actor = actor,
                    role = role,
                    action = "PROCESS_KILL",
                    targetResource = "PID $pid",
                    details = "User $actor ($role) explicitly requested process termination via step-up MFA verification.",
                    ipAddress = userIp,
                    status = "SUCCESS"
                )
            )
            true
        } catch (e: Exception) {
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = System.currentTimeMillis(),
                    actor = actor,
                    role = role,
                    action = "PROCESS_KILL",
                    targetResource = "PID $pid",
                    details = "Failed process kill attempt: ${e.localizedMessage}",
                    ipAddress = userIp,
                    status = "DENIED"
                )
            )
            false
        }
    }

    suspend fun toggleAlertRule(ruleId: String, enabled: Boolean) {
        dao.updateAlertRuleStatus(ruleId, enabled)
    }

    suspend fun logAudit(actor: String, role: String, action: String, resource: String, details: String, ip: String, status: String) {
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = System.currentTimeMillis(),
                actor = actor,
                role = role,
                action = action,
                targetResource = resource,
                details = details,
                ipAddress = ip,
                status = status
            )
        )
    }

    suspend fun addAlertRule(rule: AlertRuleEntity) {
        dao.insertAlertRules(listOf(rule))
    }

    suspend fun createReport(title: String, summaryText: String, costSavings: Float, carbonKg: Float): ReportEntity {
        val report = ReportEntity(
            id = "rep-" + UUID.randomUUID().toString().take(8),
            title = title,
            generatedAt = System.currentTimeMillis(),
            summaryText = summaryText,
            shareToken = UUID.randomUUID().toString(),
            costSavingsEst = costSavings,
            carbonKg = carbonKg
        )
        dao.insertReport(report)
        return report
    }

    suspend fun getAiAnalysis(prompt: String, gpuContext: String, redactionEnabled: Boolean): String {
        val cleanContext = if (redactionEnabled) {
            // Strip IPs, hostnames, and specific sensitive usernames
            gpuContext.replace(Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b"""), "[REDACTED_IP]")
                .replace(Regex("""[a-zA-Z0-9.-]+\.cluster\.local"""), "[REDACTED_HOST]")
                .replace("ml-engineer", "user_alpha")
                .replace("researcher-alex", "user_beta")
        } else {
            gpuContext
        }

        val systemInstruction = "You are GPU Insight AI, an expert open-source GPU cluster monitoring and performance optimization assistant. " +
                "You analyze real-time GPU telemetry, CUDA errors, thermal metrics, VRAM allocation, and LLM inference performance (vLLM, TensorRT, PyTorch). " +
                "Always provide clear, concise, actionable advice with a confidence level and safety note. Never execute destructive commands without human confirmation."

        val combinedPrompt = "GPU Cluster Telemetry Context:\n$cleanContext\n\nUser Question/Request:\n$prompt"
        return GeminiClient.askGemini(combinedPrompt, systemInstruction)
    }

    suspend fun analyzeStackTrace(logText: String, redactionEnabled: Boolean): String {
        val cleanLog = if (redactionEnabled) {
            logText.replace(Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b"""), "[REDACTED_IP]")
                .replace(Regex("""/home/[a-zA-Z0-9_-]+"""), "/home/[REDACTED_USER]")
        } else {
            logText
        }

        val systemInstruction = "You are GPU Insight AI Debug Assistant, a high-precision diagnostic engine for CUDA, PyTorch, TensorFlow, NCCL, and GPU cluster errors. " +
                "Provide a structured analysis with: 1) ROOT CAUSE ANALYSIS, 2) RECOMMENDED STEP-BY-STEP FIX, 3) OFFICIAL DOCUMENTATION / GITHUB ISSUE LINKS."

        val prompt = "Diagnose the following CUDA error / PyTorch stack trace / NCCL log:\n\n$cleanLog"
        return GeminiClient.askGemini(prompt, systemInstruction)
    }
}
