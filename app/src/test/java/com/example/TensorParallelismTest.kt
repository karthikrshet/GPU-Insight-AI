package com.example

import com.example.domain.TensorParallelismAdvisor
import org.junit.Assert.*
import org.junit.Test

class TensorParallelismTest {

    @Test fun `70B model on 8x H100 recommends TP >= 2`() {
        val c = TensorParallelismAdvisor.recommend(70.0, 8, 80, 32)
        assertTrue(c.tensorParallelSize >= 2)
        assertTrue(c.recommendation.contains("NVLink"))
    }

    @Test fun `7B model on single GPU recommends TP=1`() {
        assertEquals(1, TensorParallelismAdvisor.recommend(7.0, 1, 80, 8).tensorParallelSize)
    }

    @Test fun `405B model recommends FP8`() {
        assertTrue(TensorParallelismAdvisor.recommend(405.0, 8, 80, 1).recommendation.contains("FP8"))
    }
}
