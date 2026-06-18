package com.example

import com.example.data.model.GpuMetric
import com.example.domain.VramDefragAdvisor
import org.junit.Assert.*
import org.junit.Test

class VramDefragTest {
    private fun metric(usedMb: Long) = GpuMetric(gpuId = 0, gpuName = "H100",
        utilizationPercent = 90f, vramUsedMb = usedMb, vramTotalMb = 81920L,
        powerDrawWatts = 600f, temperatureCelsius = 75f,
        clockFrequencyMhz = 1800, fanSpeedPercent = 70f)

    @Test fun `low usage returns no suggestions`() =
        assertTrue(VramDefragAdvisor.analyze(listOf(metric(40000L))).isEmpty())

    @Test fun `critical usage returns empty_cache action`() {
        val result = VramDefragAdvisor.analyze(listOf(metric(79000L)))
        assertEquals(1, result.size)
        assertTrue(result[0].action.contains("empty_cache"))
    }
}
