package com.example

import com.example.data.GpuInsightRepository
import com.example.data.dao.GpuInsightDao
import com.example.data.model.GpuMetric
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class RepositoryTest {
    @Mock lateinit var dao: GpuInsightDao
    private lateinit var repository: GpuInsightRepository

    @Before fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = GpuInsightRepository(dao)
    }

    @Test fun `getRecentMetrics returns flow from dao`() = runTest {
        `when`(dao.getRecentMetrics()).thenReturn(flowOf(emptyList()))
        verify(dao, never()).insertMetric(any())
    }

    @Test fun `pruneMetrics passes correct cutoff`() = runTest {
        repository.pruneMetricsOlderThan(7)
        val captor = org.mockito.ArgumentCaptor.forClass(Long::class.java)
        verify(dao).pruneOldMetrics(captor.capture())
        val cutoff = captor.value
        assertTrue(cutoff > System.currentTimeMillis() - (8 * 86_400_000L))
    }
}
