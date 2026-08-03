# GPU Insight AI — Quality & Testing Strategy

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🧪 1. Testing Pyramid Overview

GPU Insight AI employs a multi-tiered automated testing architecture ensuring 100% confidence in telemetry processing, RBAC security rules, and UI responsiveness:

```
                      ┌──────────────────────┐
                      │  Roborazzi UI Visual │  (Screenshot Regression)
                      │   Snapshot Tests     │
                      └──────────┬───────────┘
                                 │
                      ┌──────────┴───────────┐
                      │  Robolectric Local   │  (JVM UI & Activity Integration)
                      │    Compose Tests     │
                      └──────────┬───────────┘
                                 │
                      ┌──────────┴───────────┐
                      │  Unit Tests & Flow   │  (Turbine + MockK + JUnit5)
                      │   Domain UseCases    │
                      └──────────────────────┘
```

---

## 🔬 2. Unit Testing & Coroutine Flow Verification

Domain UseCases and ViewModels are verified on local JVM using **JUnit**, **MockK**, and **Turbine** for testing asynchronous Kotlin `StateFlow` streams.

### Example ViewModel Telemetry Test
```kotlin
@Test
fun `selecting GPU updates telemetry stateflow correctly`() = runTest {
    val repository = mockk<GpuInsightRepository>()
    coEvery { repository.getTelemetryForGpu("gpu-01") } returns flowOf(sampleTelemetryList)
    
    val viewModel = GpuInsightViewModel(repository)
    viewModel.selectGpu("gpu-01")

    viewModel.selectedGpuTelemetry.test {
        val item = awaitItem()
        assertEquals(1, item.size)
        assertEquals("gpu-01", item.first().gpuId)
    }
}
```

---

## 📸 3. Roborazzi Visual Snapshot Testing

UI components are snapshotted across Light / Dark themes and Compact / Expanded screen sizes to prevent layout regressions.

```bash
# Verify visual snapshots against baseline
./gradlew verifyRoborazziDebug

# Record new visual baseline snapshots
./gradlew recordRoborazziDebug
```

---

## 🧹 4. Static Code Analysis & Linter Automation

- **Ktlint & Spotless:** Enforces strict Kotlin formatting.
- **Detekt:** Analyzes code complexity, magic numbers, and unused variables.

```bash
./gradlew detekt ktlintCheck
```

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
