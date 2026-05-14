package com.example.domain

import com.example.data.model.GpuMetric
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object MetricsCsvExporter {
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault())

    fun toCsv(metrics: List<GpuMetric>) = buildString {
        appendLine("timestamp,gpu_id,gpu_name,util_pct,vram_used_mb,vram_total_mb,power_w,temp_c,clock_mhz,fan_pct")
        metrics.forEach { m ->
            appendLine(listOf(
                fmt.format(Instant.ofEpochMilli(m.timestamp)), m.gpuId,
                m.gpuName.replace(",", ";"),
                m.utilizationPercent, m.vramUsedMb, m.vramTotalMb,
                m.powerDrawWatts, m.temperatureCelsius, m.clockFrequencyMhz, m.fanSpeedPercent
            ).joinToString(","))
        }
    }

    fun toMarkdown(metrics: List<GpuMetric>) = buildString {
        appendLine("| GPU | Util | VRAM | Power | Temp |")
        appendLine("|-----|------|------|-------|------|")
        metrics.forEach { m ->
            appendLine("| ${m.gpuName} | ${m.utilizationPercent.toInt()}% | " +
                       "${m.vramUsedMb}MB | ${m.powerDrawWatts.toInt()}W | ${m.temperatureCelsius.toInt()}°C |")
        }
    }
}
