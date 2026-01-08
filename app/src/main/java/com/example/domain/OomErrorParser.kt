package com.example.domain

data class ParsedOomError(
    val errorType: OomErrorType,
    val requestedMb: Long,
    val availableMb: Long,
    val frameworkHint: String,
    val suggestedFixes: List<String>
)

enum class OomErrorType { TORCH_OOM, CUDA_OOM, NCCL_TIMEOUT, CUDA_ILLEGAL_ACCESS, UNKNOWN }

object OomErrorParser {
    fun parse(stackTrace: String): ParsedOomError {
        val type = detect(stackTrace)
        val (req, avail) = extractMem(stackTrace)
        return ParsedOomError(type, req, avail, detectFramework(stackTrace), fixes(type, req))
    }

    private fun detect(t: String) = when {
        t.contains("torch.OutOfMemoryError") || t.contains("CUDA out of memory") -> OomErrorType.TORCH_OOM
        t.contains("cudaErrorIllegalAddress")  -> OomErrorType.CUDA_ILLEGAL_ACCESS
        t.contains("NCCL error")               -> OomErrorType.NCCL_TIMEOUT
        t.contains("cudaMalloc failed")        -> OomErrorType.CUDA_OOM
        else                                   -> OomErrorType.UNKNOWN
    }

    private fun extractMem(t: String): Pair<Long, Long> {
        val req   = Regex("Tried to allocate (\d+\.?\d*) (GiB|MiB)").find(t)
        val avail = Regex("\((\d+\.?\d*) (GiB|MiB) free\)").find(t)
        fun toMb(v: String, u: String) = if (u == "GiB") (v.toDouble() * 1024).toLong() else v.toLong()
        return (req?.let { toMb(it.groupValues[1], it.groupValues[2]) } ?: 0L) to
               (avail?.let { toMb(it.groupValues[1], it.groupValues[2]) } ?: 0L)
    }

    private fun detectFramework(t: String) = when {
        t.contains("torch") -> "PyTorch"
        t.contains("tensorflow") -> "TensorFlow"
        else -> "Unknown"
    }

    private fun fixes(type: OomErrorType, req: Long) = buildList {
        when (type) {
            OomErrorType.TORCH_OOM -> {
                add("Reduce batch size (try halving it)")
                add("Use torch.cuda.empty_cache() between forward passes")
                add("Enable gradient checkpointing: model.gradient_checkpointing_enable()")
                if (req > 10_000) add("Consider device_map='auto' for model sharding")
            }
            OomErrorType.CUDA_ILLEGAL_ACCESS -> add("Run with CUDA_LAUNCH_BLOCKING=1")
            OomErrorType.NCCL_TIMEOUT -> add("Check inter-GPU connectivity: nvidia-smi topo -m")
            else -> add("Enable CUDA_LAUNCH_BLOCKING=1 for details")
        }
    }
}
