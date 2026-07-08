package com.example.domain

import com.example.data.model.GpuMetric
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class ExecutiveReport(
    val title: String,
    val generatedAt: String,
    val markdownContent: String,
    val carbonReport: CarbonReport?
)

class ReportGenerator {
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault())

    fun generate(metrics: List<GpuMetric>, issues: List<String> = emptyList()): ExecutiveReport {
        val now   = fmt.format(Instant.now())
        val avgT  = metrics.map { it.temperatureCelsius }.average()
        val avgU  = metrics.map { it.utilizationPercent }.average()
        val total = metrics.sumOf { it.powerDrawWatts.toDouble() }
        val carbon = if (metrics.isNotEmpty()) CarbonCalculator.calculate(total.toFloat(), 24.0) else null

        val md = buildString {
            appendLine("# GPU Cluster Executive Report")
            appendLine("*Generated: $now*"); appendLine()
            appendLine("## Summary")
            appendLine("- GPUs: ${metrics.size}")
            appendLine("- Avg Temperature: ${"%.1f".format(avgT)}°C")
            appendLine("- Avg Utilization: ${"%.1f".format(avgU)}%")
            appendLine("- Total Power: ${"%.0f".format(total)}W")
            if (issues.isNotEmpty()) {
                appendLine(); appendLine("## Issues")
                issues.forEach { appendLine("- $it") }
            }
            carbon?.let {
                appendLine(); appendLine("## Carbon (24h)")
                appendLine("- Energy: ${"%.2f".format(it.powerConsumptionKwh)} kWh")
                appendLine("- CO2: ${"%.3f".format(it.carbonEmissionsKgCo2e)} kg CO2e")
                appendLine("- Cost: $${"%.2f".format(it.estimatedCostUsd)}")
            }
        }
        return ExecutiveReport("GPU Cluster Executive Report", now, md, carbon)
    }
}
