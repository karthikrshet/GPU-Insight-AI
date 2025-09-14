package com.example.domain

import com.example.data.model.GpuMetric

object GpuHealthCalculator {
    fun calculate(metric: GpuMetric): GpuHealth = when {
        isCritical(metric) -> GpuHealth.CRITICAL
        isWarning(metric)  -> GpuHealth.WARNING
        else               -> GpuHealth.HEALTHY
    }

    private fun isCritical(m: GpuMetric) =
        m.temperatureCelsius >= 90f ||
        m.utilizationPercent >= 99f ||
        m.vramUsedMb.toFloat() / m.vramTotalMb >= 0.98f

    private fun isWarning(m: GpuMetric) =
        m.temperatureCelsius >= 75f ||
        m.utilizationPercent >= 90f ||
        m.vramUsedMb.toFloat() / m.vramTotalMb >= 0.85f

    fun getThermalStatus(tempC: Float): String = when {
        tempC >= 90f -> "CRITICAL"
        tempC >= 75f -> "WARNING"
        tempC >= 60f -> "NORMAL"
        else         -> "COOL"
    }
}
