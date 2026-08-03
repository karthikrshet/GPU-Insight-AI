# GPU Insight AI 🚀

> **The Open Source AI-Powered GPU Infrastructure & Android Diagnostics Platform**

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_Jetpack_Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/AI-Google_Gemini_Pro-orange.svg)](https://ai.google.dev/)

**GPU Insight AI** is the world's premier open-source AI-native GPU Infrastructure platform and mobile diagnostic companion. Designed for AI engineers, ML researchers, DevOps teams, and GPU enthusiasts to monitor, diagnose, benchmark, optimize, and secure high-performance GPU clusters (NVIDIA NVLink/Fabric, AMD ROCm, Intel Gaudi) directly from Android and cloud environments.

---

## 🌟 Key Features

### ⚡ 1. Sub-Second Real-Time GPU Telemetry
- Sub-second sampling of GPU utilization, VRAM allocation, TDP power draw, fan speed, core clock frequencies, and junction temperatures.
- NVIDIA NVLink 4.0 Fabric bandwidth metrics, PCIe Gen 4/5 bus throughput, and MIG (Multi-Instance GPU) partition health.

### 🤖 2. Gemini AI Debug Assistant & OOM Troubleshooter
- Direct stack trace diagnosis for PyTorch Out-Of-Memory (`torch.OutOfMemoryError`), CUDA illegal memory access, and NCCL ring buffer timeouts.
- Privacy-first automatic secret redaction pipeline (redacts AWS keys, tokens, and internal server IPs before sending prompts to Gemini API).

### 🛡️ 3. Zero-Trust Security & RBAC Audit Logs
- Enterprise Role-Based Access Control (OWNER, ADMIN, OPERATOR, VIEWER, AUDITOR).
- Local Room Database audit event logging with SHA-256 hash chains for security compliance auditing.

### 🔔 4. WorkManager Thermal Background Daemon & Chaos Engineering
- Background thermal threshold monitor utilizing Android WorkManager to issue notifications even when the app is closed.
- Synthetic Chaos Spike Generator to simulate thermal throttles and memory pressure for testing incident response workflows.

### 📊 5. Executive Reports & Carbon Metrics
- 1-Click Executive PDF and Markdown summary generator.
- Sustainability tracking including carbon emissions (kg CO2e) and GPU cluster energy consumption (kWh).

---

## 🏗️ Architecture Overview

The app follows **Modern Android Architecture Guidelines**:
- **UI Layer:** Jetpack Compose with Material Design 3, custom Canvas charts, responsive dynamic typography, and adaptive layouts.
- **State Management:** MVVM architecture powered by `ViewModel`, `MutableStateFlow`, and `collectAsStateWithLifecycle`.
- **Data Layer:** Room Database for persistent audit logging and user settings, Proto DataStore, and Ktor/Retrofit for network gRPC/REST APIs.
- **Background Execution:** Android WorkManager for thermal threshold polling and automated alerts.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish (2023.3.1) or later
- JDK 17
- Android SDK 34 (Min SDK 26)

### Building from Source

```bash
# Clone the repository
git clone https://github.com/karthikrshet/GPU-Insight-AI.git

# Navigate to project directory
cd GPU-Insight-AI

# Build debug APK
./gradlew assembleDebug
```

---

## 📄 License & Attribution

Copyright © 2026 Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet)).

Licensed under the [Apache License, Version 2.0](LICENSE). You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
