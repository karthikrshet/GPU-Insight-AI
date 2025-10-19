package com.example

import com.example.domain.AnalyzeGpuErrorUseCase
import com.example.domain.GetGpuMetricsUseCase
import com.example.ui.viewmodel.GpuInsightViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    @Mock lateinit var getMetrics: GetGpuMetricsUseCase
    @Mock lateinit var analyzeError: AnalyzeGpuErrorUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(getMetrics()).thenReturn(flowOf(emptyList()))
    }

    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun `initial state has empty metrics and no loading`() {
        val vm = GpuInsightViewModel(getMetrics, analyzeError)
        assert(vm.uiState.value.metrics.isEmpty())
        assert(!vm.uiState.value.isLoadingAi)
        assert(vm.uiState.value.errorMessage == null)
    }
}
