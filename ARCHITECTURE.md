# GPU Insight AI — System Architecture & Specification

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 1. Product Vision & Mission
GPU Insight AI is the world’s premier open-source AI-native GPU Infrastructure platform and mobile diagnostic companion. Designed for AI engineers, ML researchers, DevOps teams, and GPU enthusiasts to monitor, diagnose, benchmark, optimize, and secure high-performance GPU clusters (NVIDIA NVLink/Fabric, AMD ROCm, Intel Gaudi) directly from Android and cloud environments.

---

## 2. Client-Server & Remote Agent Sync Architecture

Because Android devices cannot directly communicate with hardware NVML registers on remote server nodes over PCIe, the application functions as a **Client-Server Architecture**:

```
┌─────────────────────────────────────────────────────────┐
│              Android Mobile Client Application           │
│  ┌───────────────────────┐   ┌───────────────────────┐  │
│  │   Jetpack Compose UI  │   │  Room DB (Offline)    │  │
│  └───────────┬───────────┘   └───────────▲───────────┘  │
│              │                           │              │
│              ▼                           │ (Cache Sync) │
│  ┌───────────────────────────────────────┴───────────┐  │
│  │         Domain UseCases & Repository              │  │
│  └───────────────────────┬───────────────────────────┘  │
└──────────────────────────┼──────────────────────────────┘
                           │ gRPC / HTTPS REST (Sync 100ms - 1s)
                           ▼
┌─────────────────────────────────────────────────────────┐
│        Remote Host GPU Exporter Agent (Linux / Windows)  │
│  ┌───────────────────────┐   ┌───────────────────────┐  │
│  │ NVIDIA NVML C-API /   │   │ AMD ROCm SMI /        │  │
│  │ TensorRT Telemetry    │   │ Intel Gaudi Telemetry │  │
│  └───────────────────────┘   └───────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Key Offline-First Sync Strategies:
1. **Active Online Sync:** Continuous polling or gRPC streaming from remote host agents at adjustable intervals (100ms to 5000ms).
2. **Offline Local Fallback:** When internet or host connectivity is interrupted, the app automatically transitions to local Room Database cached metrics without flickering or UI blockage.
3. **Delta Sync & Reconciliation:** Upon reconnection, the repository reconciles local offline audit logs with the remote cluster audit ledger.

---

## 3. Feature-Based Modular Architecture

To maintain scale, isolation, fast build times, and clean boundary enforcement, the codebase is structured around feature modules:

```
GPU-Insight-AI /
├── app /                         # Main Application Entry Point & Navigation Host
├── core /
│   ├── model /                   # Shared Domain Entities & State Models
│   ├── data /                    # Room Database, Repositories & DataSources
│   ├── domain /                  # Clean Architecture UseCases & Interfaces
│   ├── network /                 # Ktor / Retrofit & Gemini API Clients
│   ├── security /                # Android Keystore, SQLCipher & Biometric Auth
│   └── ui /                      # Theme Tokens, Common Composables & Design System
└── feature /
    ├── dashboard /               # Real-Time Telemetry & GPU Charts
    ├── processes /               # Process List & MFA Kill Workflows
    ├── ai-advisor /              # Gemini Debug Assistant & Stack Trace Analyzer
    ├── alerts /                  # WorkManager Background Thermal Monitor
    └── security-reports /        # Audit Logs & 1-Click Executive PDF Generator
```

---

## 4. Layered Clean Architecture

Each feature module enforces strict unidirectional data flow (UDF):

```
       ┌────────────────────────┐
       │   Jetpack Compose UI   │
       └───────────▲────────────┘
                   │ StateFlow<UiState> / Events
       ┌───────────┴────────────┐
       │       ViewModel        │
       └───────────▲────────────┘
                   │ Result<T> / Flow<T>
       ┌───────────┴────────────┐
       │    Domain UseCases     │
       └───────────▲────────────┘
                   │ Repository Interfaces
       ┌───────────┴────────────┐
       │    Data Repository     │
       └──────┬───────────┬─────┘
              │           │
              ▼           ▼
       ┌─────────────┐  ┌──────────────┐
       │  Room DAO   │  │ Remote Agent │
       │  (Database) │  │  (Ktor/gRPC) │
       └─────────────┘  └──────────────┘
```

### Key Domain UseCases:
- `GetGpuTelemetryUseCase`: Returns real-time flow of metric telemetry.
- `SyncRemoteAgentGpuUseCase`: Manages background synchronization with remote NVML exporter agents.
- `AnalyzeGpuStacktraceUseCase`: Invokes Gemini API with automated secret redaction.
- `TerminateGpuProcessUseCase`: Executes multi-factor authenticated process termination.

---

## 5. Security & Privacy Architecture

1. **Android Keystore & Encrypted DataStore:** Master key generation using `AES256_GCM` in Android Keystore to encrypt OAuth tokens and API credentials.
2. **SQLCipher Database Encryption:** Encrypts local Room Database tables containing security audit logs.
3. **Biometric Auth & MFA Token:** Step-up authentication required for privileged operations (`Kill Process`, `Modify Alert Rules`).
4. **Secret Redaction Pipeline (`SecretRedactor`):** Sanitizes stack traces before transmitting to LLMs.

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
