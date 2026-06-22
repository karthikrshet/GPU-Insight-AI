package com.example.domain

data class CudaEnvironment(
    val cudaVersion: String,
    val computeCapability: String,
    val isCompatible: Boolean,
    val notes: String
)

object CudaVersionDetector {
    private val PYTORCH_CUDA_COMPAT = mapOf(
        "2.3" to "12.1", "2.2" to "12.1", "2.1" to "11.8",
        "2.0" to "11.8", "1.13" to "11.7"
    )

    fun check(pytorchVersion: String, cudaVersion: String, computeCapability: String): CudaEnvironment {
        val required = PYTORCH_CUDA_COMPAT[pytorchVersion]
        val compat = required != null && cudaVersion >= required
        val notes = when {
            !compat -> "PyTorch $pytorchVersion requires CUDA $required, found $cudaVersion"
            computeCapability < "8.0" -> "CC $computeCapability may not support BF16/FP8"
            else -> "Environment compatible"
        }
        return CudaEnvironment(cudaVersion, computeCapability, compat, notes)
    }

    fun minComputeCapability(feature: String) = when (feature) {
        "FP8" -> "9.0"; "BF16" -> "8.0"; "TF32" -> "8.0"; "INT8" -> "7.5"; else -> "6.0"
    }
}
