package com.example

import com.example.data.model.GpuMetric
import com.example.domain.MetricsCsvExporter
import org.junit.Assert.*
import org.junit.Test

class MetricsCsvExporterTest {
    private val sample = GpuMetric(gpuId = 0, gpuName = "NVIDIA H100",
        utilizationPercent = 87.5f, vramUsedMb = 65536, vramTotalMb = 81920,
        powerDrawWatts = 650f, temperatureCelsius = 78f, clockFrequencyMhz = 1755, fanSpeedPercent = 75f)

    @Test fun `CSV header is correct`() {
        assertTrue(MetricsCsvExporter.toCsv(listOf(sample)).startsWith("timestamp,gpu_id"))
    }

    @Test fun `CSV has correct row count`() {
        val lines = MetricsCsvExporter.toCsv(listOf(sample, sample)).trim().split("
")
        assertEquals(3, lines.size)
    }

    @Test fun `markdown includes GPU name`() {
        assertTrue(MetricsCsvExporter.toMarkdown(listOf(sample)).contains("NVIDIA H100"))
    }
}
