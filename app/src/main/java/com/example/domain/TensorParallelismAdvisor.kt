package com.example.domain

data class TpConfig(
    val tensorParallelSize: Int,
    val pipelineParallelSize: Int,
    val dataParallelSize: Int,
    val estimatedThroughputTps: Int,
    val recommendation: String
)

/**
 * Recommends optimal tensor/pipeline parallelism for LLM deployment on NVIDIA clusters.
 */
object TensorParallelismAdvisor {
    fun recommend(modelParamsB: Double, availableGpus: Int, vramPerGpuGb: Int, batchSize: Int): TpConfig {
        val estimatedVramGb = modelParamsB * 2  // FP16
        val minGpus = (estimatedVramGb / vramPerGpuGb).toInt() + 1
        val tp = when {
            minGpus <= 1 -> 1; minGpus <= 2 -> 2; minGpus <= 4 -> 4; else -> 8
        }.coerceAtMost(availableGpus)
        val pp = (availableGpus / tp).coerceAtLeast(1)
        val rec = buildString {
            appendLine("Model: ${modelParamsB}B params ~${estimatedVramGb}GB VRAM")
            appendLine("Recommended: TP=$tp, PP=$pp")
            appendLine("Use NVLink for TP (intra-node), InfiniBand for PP (inter-node)")
            if (modelParamsB > 70) appendLine("Consider FP8 quantization to halve VRAM")
        }
        return TpConfig(tp, pp, 1, (tp * 1000 / (modelParamsB * 0.1)).toInt(), rec)
    }
}
