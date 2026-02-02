package com.example.domain

/**
 * Parser for NVIDIA Data Center GPU Manager (DCGM) metrics.
 * DCGM is the industry standard for enterprise GPU monitoring in HPC/AI data centers.
 */
data class DcgmMetric(
    val fieldId: Int,
    val fieldName: String,
    val value: Double,
    val timestamp: Long,
    val gpuId: Int
)

object DcgmMetricsParser {
    private val FIELD_NAMES = mapOf(
        100 to "DCGM_FI_DEV_GPU_UTIL",
        150 to "DCGM_FI_DEV_POWER_USAGE",
        155 to "DCGM_FI_DEV_GPU_TEMP",
        190 to "DCGM_FI_DEV_FB_USED",
        191 to "DCGM_FI_DEV_FB_FREE",
        201 to "DCGM_FI_DEV_ECC_SBE_VOL_TOTAL",
        202 to "DCGM_FI_DEV_ECC_DBE_VOL_TOTAL"
    )

    fun parseCsvLine(csv: String): DcgmMetric? = runCatching {
        val parts = csv.split(",")
        DcgmMetric(
            fieldId   = parts[0].trim().toInt(),
            fieldName = FIELD_NAMES[parts[0].trim().toInt()] ?: "UNKNOWN_FIELD",
            value     = parts[1].trim().toDouble(),
            timestamp = parts[2].trim().toLong(),
            gpuId     = parts[3].trim().toInt()
        )
    }.getOrNull()

    fun getEccErrors(metrics: List<DcgmMetric>): Map<String, Double> =
        metrics.filter { it.fieldId in listOf(201, 202) }
            .associate { it.fieldName to it.value }
}
