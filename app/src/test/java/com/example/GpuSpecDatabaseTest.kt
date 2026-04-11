package com.example

import com.example.domain.GpuSpecDatabase
import org.junit.Assert.*
import org.junit.Test

class GpuSpecDatabaseTest {

    @Test fun `H100 SXM5 spec is correct`() {
        val spec = GpuSpecDatabase.specs["H100 SXM5"]!!
        assertEquals(80, spec.vramGb)
        assertEquals(700, spec.tdpWatts)
        assertEquals(3350, spec.memBandwidthGbps)
        assertEquals("Hopper", spec.architecture)
    }

    @Test fun `findByName returns correct spec`() {
        val spec = GpuSpecDatabase.findByName("A100")
        assertNotNull(spec); assertEquals("Ampere", spec!!.architecture)
    }

    @Test fun `H200 has 141GB VRAM`() {
        assertEquals(141, GpuSpecDatabase.specs["H200 SXM"]!!.vramGb)
    }

    @Test fun `findByName returns null for unknown GPU`() {
        assertNull(GpuSpecDatabase.findByName("GTX 1080"))
    }
}
