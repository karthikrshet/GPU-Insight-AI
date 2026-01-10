package com.example

import com.example.domain.OomErrorParser
import com.example.domain.OomErrorType
import org.junit.Assert.*
import org.junit.Test

class OomErrorParserTest {

    @Test fun `detects PyTorch OOM`() {
        val trace = "RuntimeError: CUDA out of memory. Tried to allocate 2.50 GiB " +
                    "(GPU 0; 1.25 GiB free; torch.OutOfMemoryError)"
        val result = OomErrorParser.parse(trace)
        assertEquals(OomErrorType.TORCH_OOM, result.errorType)
        assertEquals("PyTorch", result.frameworkHint)
        assertTrue(result.suggestedFixes.isNotEmpty())
    }

    @Test fun `detects NCCL timeout`() {
        val result = OomErrorParser.parse("NCCL error in /pytorch/torch/csrc/distributed/c10d/ProcessGroupNCCL.cpp")
        assertEquals(OomErrorType.NCCL_TIMEOUT, result.errorType)
    }

    @Test fun `returns UNKNOWN for unrecognized trace`() {
        assertEquals(OomErrorType.UNKNOWN, OomErrorParser.parse("Weird thing happened").errorType)
    }
}
