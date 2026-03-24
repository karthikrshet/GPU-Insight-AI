# GPU Insight AI 🚀

> **The Open Source AI-Powered GPU Infrastructure & Android Diagnostics Platform**

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_Jetpack_Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/AI-Google_Gemini_Pro-orange.svg)](https://ai.google.dev/)
[![CI](https://github.com/karthikrshet/GPU-Insight-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/karthikrshet/GPU-Insight-AI/actions)

**GPU Insight AI** is the world's premier open-source AI-native GPU Infrastructure platform and mobile diagnostic companion. Designed for AI engineers, ML researchers, DevOps teams, and GPU enthusiasts to monitor, diagnose, benchmark, optimize, and secure high-performance GPU clusters (NVIDIA NVLink/Fabric, AMD ROCm, Intel Gaudi) directly from Android.

---

## Key Features

### 1. Sub-Second Real-Time GPU Telemetry
- 500ms sampling of utilization, VRAM, TDP, fan speed, clock frequencies, junction temperatures
- NVIDIA NVLink 4.0 bandwidth metrics, PCIe Gen 4/5 throughput, MIG partition health
- DCGM field parsing with ECC SBE/DBE error monitoring

### 2. Gemini AI Debug Assistant & OOM Troubleshooter
- Stack trace diagnosis for torch.OutOfMemoryError, CUDA illegal access, NCCL timeouts
- Privacy-first secret redaction (AWS keys, Bearer tokens, internal IPs)

### 3. Zero-Trust Security & RBAC Audit Logs
- 5-tier RBAC: OWNER, ADMIN, OPERATOR, VIEWER, AUDITOR
- SHA-256 hash chain audit event log for compliance

### 4. WorkManager Thermal Background Daemon
- Persistent thermal alerts even when app is closed
- Chaos Engineering: synthetic thermal/OOM simulation

### 5. Executive Reports & Carbon Metrics
- Markdown/PDF executive report generator
- Carbon footprint tracking (kg CO2e, kWh, cost)

### 6. Advanced NVIDIA Features
- NVLink topology visualizer | Tensor Parallelism Advisor
- MIG partition manager | InfiniBand health monitor
- ECC error assessment | PCIe Gen4/5 bottleneck detection

---

## Architecture
- **UI**: Jetpack Compose + Material3, animated gauges, Canvas heatmaps
- **State**: MVVM + StateFlow + debounce for perf
- **Data**: Room DB, DataStore, OkHttp
- **DI**: Hilt
- **Background**: WorkManager with exponential backoff

## Getting Started
```bash
git clone https://github.com/karthikrshet/GPU-Insight-AI.git
cd GPU-Insight-AI
./gradlew assembleDebug
./gradlew test
```

## License
Apache License 2.0 — Copyright 2026 Karthik Rajesh Shet
