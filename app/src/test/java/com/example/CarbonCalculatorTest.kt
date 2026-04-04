package com.example

import com.example.domain.CarbonCalculator
import org.junit.Assert.*
import org.junit.Test

class CarbonCalculatorTest {

    @Test fun `zero power produces zero carbon`() {
        val r = CarbonCalculator.calculate(0f, 24.0)
        assertEquals(0.0, r.powerConsumptionKwh, 0.001)
        assertEquals(0.0, r.carbonEmissionsKgCo2e, 0.001)
    }

    @Test fun `H100 700W for 24h emits reasonable CO2`() {
        val r = CarbonCalculator.calculate(700f, 24.0)
        // 700W * 24h = 16.8 kWh * 0.417 = ~7.0 kg CO2e
        assertTrue(r.carbonEmissionsKgCo2e in 6.0..8.0)
        assertEquals(16.8, r.powerConsumptionKwh, 0.1)
    }

    @Test fun `8x H100 cluster 24h cost estimate`() {
        val r = CarbonCalculator.calculate(8 * 700f, 24.0, electricityCostPerKwh = 0.10)
        assertTrue(r.estimatedCostUsd > 10.0)
    }
}
