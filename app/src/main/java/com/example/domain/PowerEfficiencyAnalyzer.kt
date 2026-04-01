package com.example.domain

data class EfficiencyReport(
    val gpuId: Int,
    val gpuName: String,
    val tflopsPerWatt: Double,
    val efficiencyRating: String,
    val savingsOpportunityPercent: Float
)

object PowerEfficiencyAnalyzer {
    // H100 SXM5 FP16 reference: ~5.6 TFLOPS/W
    private const val H100_REF_TFLOPS_PER_WATT = 5.6

    fun analyze(gpuId: Int, gpuName: String, tflops: Double, watts: Float): EfficiencyReport {
        val tfw = if (watts > 0) tflops / watts else 0.0
        val rating = when {
            tfw >= H100_REF_TFLOPS_PER_WATT * 0.9 -> "Excellent"
            tfw >= H100_REF_TFLOPS_PER_WATT * 0.7 -> "Good"
            tfw >= H100_REF_TFLOPS_PER_WATT * 0.5 -> "Fair"
            else -> "Poor"
        }
        val savings = ((H100_REF_TFLOPS_PER_WATT - tfw) / H100_REF_TFLOPS_PER_WATT * 100)
            .toFloat().coerceAtLeast(0f)
        return EfficiencyReport(gpuId, gpuName, tfw, rating, savings)
    }

    fun rank(reports: List<EfficiencyReport>): List<EfficiencyReport> =
        reports.sortedByDescending { it.tflopsPerWatt }
}
