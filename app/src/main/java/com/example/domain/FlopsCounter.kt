package com.example.domain

/**
 * Estimates FLOPs for common ML operations.
 * Used for GPU utilization efficiency benchmarking.
 */
object FlopsCounter {
    /** Forward pass estimate: 2 * params * seq_len * batch */
    fun transformerForwardFlops(params: Long, seqLen: Int, batch: Int): Long =
        2L * params * seqLen * batch

    /** Matmul: 2 * M * N * K */
    fun matmulFlops(m: Int, n: Int, k: Int): Long = 2L * m * n * k

    /** Multi-head attention FLOPs */
    fun attentionFlops(seqLen: Int, hiddenDim: Int, numHeads: Int, batch: Int): Long {
        val headDim = hiddenDim / numHeads
        val qkv = 3L * matmulFlops(seqLen, hiddenDim, hiddenDim)
        val attn = batch * numHeads * matmulFlops(seqLen, seqLen, headDim).toLong()
        val out  = matmulFlops(seqLen, hiddenDim, hiddenDim)
        return (qkv + attn + out) * batch
    }

    /** Convert FLOPs to TFLOPS given duration in ms */
    fun tflops(flops: Long, durationMs: Long): Double = flops.toDouble() / (durationMs * 1e9)
}
