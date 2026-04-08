package com.example.domain

data class GpuSpec(
    val model: String,
    val architecture: String,
    val vramGb: Int,
    val peakFp16Tflops: Double,
    val peakFp8Tflops: Double,
    val tdpWatts: Int,
    val nvlinkBandwidthGbps: Int,
    val memBandwidthGbps: Int,
    val pcieGen: Int
)

object GpuSpecDatabase {
    val specs = mapOf(
        "H100 SXM5"  to GpuSpec("NVIDIA H100 SXM5",  "Hopper",      80, 3958.0, 7916.0, 700, 900, 3350, 5),
        "H100 PCIe"  to GpuSpec("NVIDIA H100 PCIe",  "Hopper",      80, 2996.0, 5992.0, 350, 0,   2000, 5),
        "A100 SXM4"  to GpuSpec("NVIDIA A100 SXM4",  "Ampere",      80, 2496.0, 4992.0, 400, 600, 2000, 4),
        "RTX 4090"   to GpuSpec("NVIDIA RTX 4090",   "Ada Lovelace",24, 1321.0, 2642.0, 450, 0,   1008, 4),
        "L40S"       to GpuSpec("NVIDIA L40S",        "Ada Lovelace",48, 733.0,  1466.0, 350, 0,   864,  4),
        "H200 SXM"   to GpuSpec("NVIDIA H200 SXM",   "Hopper",      141,3958.0, 7916.0, 700, 900, 4800, 5)
    )

    fun findByName(name: String): GpuSpec? =
        specs.values.find { it.model.contains(name, ignoreCase = true) }
}
