# Plugin Development Guide

GPU Insight AI supports custom data source plugins.

## Creating a Plugin

```kotlin
interface GpuDataSource {
    val name: String
    suspend fun getMetrics(): List<GpuMetric>
    suspend fun isAvailable(): Boolean
}
```

## Built-in Plugins
- **SysfsPlugin**: Linux /sys/class/drm scraper
- **MockPlugin**: Synthetic data for testing
- **DcgmPlugin**: NVIDIA DCGM exporter integration
- **PrometheusPlugin**: Prometheus metrics scraper

## Plugin Lifecycle
1. isAvailable() — checked on startup
2. getMetrics() — called every 500ms by telemetry loop
3. Results merged into the unified metric stream
