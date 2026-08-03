# GPU Insight AI — Plugin SDK & Developer Guide

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🔌 1. Plugin Architecture Overview

GPU Insight AI includes an extensible **Plugin SDK** that allows third-party developers, hardware vendors, and enterprise DevOps teams to register custom GPU telemetry collectors, custom alert webhook dispatchers, and custom diagnostic log parsers.

```
┌─────────────────────────────────────────────────────────────┐
│                   GPU Insight AI Core Plugin SDK             │
│  ┌───────────────────────┐       ┌───────────────────────┐  │
│  │ TelemetryCollector    │       │ AlertWebhookChannel   │  │
│  └───────────▲───────────┘       └───────────▲───────────┘  │
└──────────────┼───────────────────────────────┼──────────────┘
               │                               │
┌──────────────┴───────────┐       ┌───────────┴──────────────┐
│  Apple Silicon Metal     │       │  PagerDuty / Slack       │
│  Telemetry Plugin        │       │  Alert Plugin            │
└──────────────────────────┘       └──────────────────────────┘
```

---

## 🛠️ 2. Writing a Custom Telemetry Collector Plugin

Developers can implement the `GpuTelemetryPlugin` interface to support custom hardware, such as Apple Silicon Metal GPUs or custom FPGA accelerators:

```kotlin
package com.example.plugin.api

import com.example.data.model.GpuEntity
import com.example.data.model.MetricTelemetryEntity

interface GpuTelemetryPlugin {
    val pluginId: String
    val pluginName: String
    val supportedVendor: String

    suspend fun discoverGpus(): List<GpuEntity>
    suspend fun pollTelemetry(gpuId: String): MetricTelemetryEntity
}
```

### Example Custom Plugin Registration
```kotlin
class AppleMetalPlugin : GpuTelemetryPlugin {
    override val pluginId = "com.vendor.apple.metal"
    override val pluginName = "Apple Silicon Metal Telemetry"
    override val supportedVendor = "Apple"

    override suspend fun discoverGpus(): List<GpuEntity> {
        return listOf(
            GpuEntity(
                id = "apple-m3-max",
                nodeOwnerId = "local-macbook",
                name = "Apple M3 Max (40-Core GPU)",
                vendor = "Apple",
                architecture = "Apple Silicon",
                driverVersion = "macOS 15.1",
                cudaVersion = "Metal 3"
            )
        )
    }

    override suspend fun pollTelemetry(gpuId: String): MetricTelemetryEntity {
        return MetricTelemetryEntity(
            gpuId = gpuId,
            timestamp = System.currentTimeMillis(),
            temperatureGpu = 42.0f,
            temperatureMemory = 45.0f,
            vramUsedMb = 24500,
            vramTotalMb = 131072,
            powerDrawWatts = 48.2f,
            powerLimitWatts = 100.0f,
            smClockMhz = 1400,
            memClockMhz = 3200,
            fanSpeedPercent = 35.0f
        )
    }
}
```

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
