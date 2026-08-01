# GPU Insight AI 🚀

> **The Open Source AI-Powered GPU Infrastructure & Android Diagnostics Platform**

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_Jetpack_Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/AI-Google_Gemini_Pro-orange.svg)](https://ai.google.dev/)
[![CI](https://github.com/karthikrshet/GPU-Insight-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/karthikrshet/GPU-Insight-AI/actions)

**GPU Insight AI** is the world's premier open-source AI-native GPU Infrastructure platform and mobile diagnostic companion. Designed for AI engineers, ML researchers, DevOps teams, and GPU enthusiasts to monitor, diagnose, benchmark, optimize, and secure high-performance GPU clusters (NVIDIA NVLink/Fabric, AMD ROCm) directly from Android.

---

## Key Features

### Sub-Second Real-Time GPU Telemetry
- 500ms sampling of utilization, VRAM, TDP, fan speed, core clocks, junction temperatures
- NVIDIA NVLink 4.0 bandwidth, PCIe Gen4/5 throughput, MIG partition health
- DCGM field parsing with ECC SBE/DBE error monitoring

### Gemini AI Debug Assistant & OOM Troubleshooter
- Diagnoses torch.OutOfMemoryError, CUDA illegal access, NCCL timeouts
- Privacy-first: auto-redacts AWS keys, Bearer tokens, internal IPs before API calls
- Smart OOM parser with framework detection (PyTorch, TensorFlow, JAX)

### Zero-Trust Security & RBAC
- 5-tier RBAC: OWNER, ADMIN, OPERATOR, VIEWER, AUDITOR
- SHA-256 hash chain audit log for compliance

### WorkManager Thermal Daemon + Chaos Engineering
- Persistent thermal alerts (even when app is closed)
- Synthetic thermal/OOM chaos simulation

### Executive Reports & Carbon Metrics
- Markdown/PDF generator with CO2 and kWh tracking

### Advanced NVIDIA Features
- Tensor Parallelism Advisor (TP/PP for LLM deployment)
- NVLink topology visualizer | MIG partition tracker
- InfiniBand link health | ECC error assessment
- VRAM defrag advisor | GPU spec database (H100, A100, L40S, H200)
- Triton Inference Server health checker

---

## Architecture
- **UI**: Jetpack Compose, Material3, animated Canvas charts
- **State**: MVVM + StateFlow + 200ms debounce
- **Data**: Room DB, DataStore, OkHttp
- **DI**: Hilt | **Background**: WorkManager + exponential backoff
- **CI/CD**: GitHub Actions

## Getting Started
```bash
git clone https://github.com/karthikrshet/GPU-Insight-AI.git
cd GPU-Insight-AI
cp .env.example local.properties  # add your GEMINI_API_KEY
./gradlew assembleDebug
./gradlew test
```

## License
Apache License 2.0 — Copyright 2026 Karthik Rajesh Shet
