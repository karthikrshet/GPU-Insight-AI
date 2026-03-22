package com.example.domain

import com.example.data.model.GpuMetric
import kotlinx.coroutines.delay
import javax.inject.Inject

data class BenchmarkResult(
    val gpuId: Int,
    val gpuName: String,
    val peakTflops: Double,
    val memoryBandwidthGbps: Double,
    val averageUtilization: Float,
    val thermalHeadroomC: Float,
    val score: Int
)

class GpuBenchmarkUseCase @Inject constructor() {
    suspend fun run(metric: GpuMetric): BenchmarkResult {
        delay(2000) // simulate benchmark
        val tflops = estimateTflops(metric)
        val bw     = estimateBandwidth(metric)
        val headroom = 95f - metric.temperatureCelsius
        return BenchmarkResult(
            gpuId                = metric.gpuId,
            gpuName              = metric.gpuName,
            peakTflops           = tflops,
            memoryBandwidthGbps  = bw,
            averageUtilization   = metric.utilizationPercent,
            thermalHeadroomC     = headroom,
            score                = (tflops * 10 + bw * 0.5 + headroom.toDouble()).toInt()
        )
    }

    private fun estimateTflops(m: GpuMetric): Double = when {
        m.gpuName.contains("H100") -> 3958.0
        m.gpuName.contains("A100") -> 2496.0
        m.gpuName.contains("RTX 4090") -> 1321.0
        else -> m.clockFrequencyMhz * 0.001 * 100.0
    } * (m.utilizationPercent / 100.0)

    private fun estimateBandwidth(m: GpuMetric): Double = when {
        m.gpuName.contains("H100") -> 3350.0
        m.gpuName.contains("A100") -> 2000.0
        else -> 900.0
    }
}
