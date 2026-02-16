package com.example.domain

data class EccErrorReport(
    val gpuId: Int,
    val singleBitErrors: Long,
    val doubleBitErrors: Long,
    val riskLevel: EccRiskLevel,
    val recommendation: String
)

enum class EccRiskLevel { HEALTHY, WATCH, REPLACE }

object EccErrorMonitor {
    fun assess(gpuId: Int, sbe: Long, dbe: Long): EccErrorReport {
        val risk = when {
            dbe > 0    -> EccRiskLevel.REPLACE
            sbe > 1000 -> EccRiskLevel.WATCH
            else       -> EccRiskLevel.HEALTHY
        }
        val rec = when (risk) {
            EccRiskLevel.REPLACE -> "GPU #$gpuId has double-bit ECC errors. Schedule replacement. Drain workloads."
            EccRiskLevel.WATCH   -> "GPU #$gpuId has elevated SBE count ($sbe). Monitor closely."
            EccRiskLevel.HEALTHY -> "ECC errors within normal range."
        }
        return EccErrorReport(gpuId, sbe, dbe, risk, rec)
    }
}
