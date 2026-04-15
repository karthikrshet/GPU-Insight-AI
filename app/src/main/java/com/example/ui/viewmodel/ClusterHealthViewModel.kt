package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClusterHealthState(
    val totalGpus: Int       = 0,
    val healthyCount: Int    = 0,
    val warningCount: Int    = 0,
    val criticalCount: Int   = 0,
    val overallHealth: GpuHealth = GpuHealth.UNKNOWN,
    val avgTemperature: Float = 0f,
    val avgUtilization: Float = 0f,
    val totalPowerWatts: Float = 0f,
    val isLoading: Boolean   = true
)

class ClusterHealthViewModel @Inject constructor(
    private val getMetrics: GetGpuMetricsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClusterHealthState())
    val state: StateFlow<ClusterHealthState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getMetrics()
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { metrics ->
                    if (metrics.isEmpty()) {
                        _state.update { ClusterHealthState(isLoading = false) }
                        return@collect
                    }
                    val healths = metrics.map { GpuHealthCalculator.calculate(it) }
                    _state.update {
                        ClusterHealthState(
                            totalGpus      = metrics.size,
                            healthyCount   = healths.count { h -> h == GpuHealth.HEALTHY },
                            warningCount   = healths.count { h -> h == GpuHealth.WARNING },
                            criticalCount  = healths.count { h -> h == GpuHealth.CRITICAL },
                            overallHealth  = when {
                                healths.any { h -> h == GpuHealth.CRITICAL } -> GpuHealth.CRITICAL
                                healths.any { h -> h == GpuHealth.WARNING  } -> GpuHealth.WARNING
                                else -> GpuHealth.HEALTHY
                            },
                            avgTemperature  = metrics.map { m -> m.temperatureCelsius }.average().toFloat(),
                            avgUtilization  = metrics.map { m -> m.utilizationPercent }.average().toFloat(),
                            totalPowerWatts = metrics.sumOf { m -> m.powerDrawWatts.toDouble() }.toFloat(),
                            isLoading = false
                        )
                    }
                }
        }
    }
}
