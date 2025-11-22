package com.example.domain

data class CarbonReport(
    val powerConsumptionKwh: Double,
    val carbonEmissionsKgCo2e: Double,
    val estimatedCostUsd: Double
)

object CarbonCalculator {
    // 2025 global average grid intensity
    private const val GRID_INTENSITY_KG_PER_KWH = 0.417

    fun calculate(
        powerDrawWatts: Float,
        durationHours: Double,
        gridIntensity: Double = GRID_INTENSITY_KG_PER_KWH,
        electricityCostPerKwh: Double = 0.12
    ): CarbonReport {
        val energyKwh = (powerDrawWatts / 1000.0) * durationHours
        return CarbonReport(
            powerConsumptionKwh   = energyKwh,
            carbonEmissionsKgCo2e = energyKwh * gridIntensity,
            estimatedCostUsd      = energyKwh * electricityCostPerKwh
        )
    }

    fun formatReport(report: CarbonReport): String = buildString {
        appendLine("=== Carbon & Energy Report ===")
        appendLine("Energy: ${"%.2f".format(report.powerConsumptionKwh)} kWh")
        appendLine("CO2:    ${"%.3f".format(report.carbonEmissionsKgCo2e)} kg CO2e")
        appendLine("Cost:   $${"%.2f".format(report.estimatedCostUsd)}")
    }
}
