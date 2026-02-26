package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TelemetryPoller
import com.example.data.model.GpuMetric
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GpuInsightUiState(
    val metrics: List<GpuMetric> = emptyList(),
    val selectedGpuId: Int       = 0,
    val health: GpuHealth        = GpuHealth.UNKNOWN,
    val aiResponse: String       = "",
    val isLoadingAi: Boolean     = false,
    val errorMessage: String?    = null,
    val isPolling: Boolean       = false
)

class GpuInsightViewModel @Inject constructor(
    private val getMetrics:      GetGpuMetricsUseCase,
    private val analyzeError:    AnalyzeGpuErrorUseCase,
    private val telemetryPoller: TelemetryPoller
) : ViewModel() {

    private val _uiState = MutableStateFlow(GpuInsightUiState())
    val uiState: StateFlow<GpuInsightUiState> = _uiState.asStateFlow()

    init {
        observeMetrics()
        telemetryPoller.startPolling(viewModelScope)
        _uiState.update { it.copy(isPolling = true) }
    }

    private fun observeMetrics() = viewModelScope.launch {
        getMetrics()
            .debounce(200L)
            .distinctUntilChanged()
            .collect { metrics -> _uiState.update { it.copy(metrics = metrics) } }
    }

    fun analyzeError(stackTrace: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoadingAi = true, errorMessage = null) }
        analyzeError.invoke(stackTrace)
            .onSuccess { r -> _uiState.update { it.copy(aiResponse = r, isLoadingAi = false) } }
            .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message, isLoadingAi = false) } }
    }

    fun selectGpu(gpuId: Int) = _uiState.update { it.copy(selectedGpuId = gpuId) }

    override fun onCleared() {
        super.onCleared()
        telemetryPoller.stopPolling()
    }
}
