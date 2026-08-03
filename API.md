# GPU Insight AI — API Specification

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🌐 Overview & Client-Server Remote Sync Architecture

Android devices cannot directly query desktop or datacenter NVIDIA / AMD / Intel GPU hardware registers via NVML over PCIe. Therefore, **GPU Insight AI** acts as a high-performance **Android Client** that interfaces with a lightweight local or remote **GPU Exporter Agent** (running on Linux/Windows workstations or Kubernetes cluster nodes) via gRPC or REST HTTPS.

```
┌────────────────────────────────┐         gRPC / REST (HTTPS)        ┌──────────────────────────────┐
│  GPU Insight AI Android Client │ ◄────────────────────────────────► │ Desktop/Server NVML Exporter │
│  (Room Cached, Offline-First)  │   Sync Interval: 100ms - 1000ms   │ (Linux/Windows Host Agent)   │
└────────────────────────────────┘                                    └──────────────────────────────┘
```

When offline or disconnected, the Android application gracefully serves cached telemetry logs from its local **Room Database**, attempting automatic background re-synchronization when host connectivity is restored.

---

## 📡 1. Remote GPU Exporter REST / gRPC API

### Base URL
`https://<agent-host>:<port>/v1`

### Authentication
All requests require an `Authorization` header containing a Bearer token or custom HMAC signature generated via Android Keystore.
```http
Authorization: Bearer <agent_jwt_token>
X-Client-Signature: <hmac_sha256_signature>
```

---

### `GET /v1/telemetry`
Fetches real-time GPU hardware telemetry for all attached devices.

#### Response (`200 OK`)
```json
{
  "timestamp": 1754228400000,
  "cluster_id": "us-west-cluster-01",
  "node_id": "node-nv-01",
  "gpus": [
    {
      "gpu_id": "gpu-01",
      "name": "NVIDIA H100 SXM5 80GB",
      "vendor": "NVIDIA",
      "architecture": "Hopper",
      "temperature_gpu": 58.5,
      "temperature_memory": 62.1,
      "vram_used_mb": 61440,
      "vram_total_mb": 81920,
      "power_draw_watts": 340.2,
      "power_limit_watts": 700.0,
      "sm_clock_mhz": 1980,
      "mem_clock_mhz": 2619,
      "fan_speed_percent": 65.0,
      "nvlink_bandwidth_gbps": 900.0,
      "pcie_throughput_mbps": 32000.0,
      "ecc_errors": 0,
      "mig_mode": "DISABLED"
    }
  ]
}
```

---

### `GET /v1/processes`
Fetches active compute processes running on the specified GPU device.

#### Query Parameters
- `gpu_id` (required): Unique ID of the GPU device (e.g. `gpu-01`).

#### Response (`200 OK`)
```json
{
  "gpu_id": "gpu-01",
  "processes": [
    {
      "pid": 28410,
      "app_name": "python3 (torch_distributed_trainer.py)",
      "user": "ai_engineer",
      "vram_used_mb": 42100,
      "sm_utilization_percent": 94.2,
      "cpu_utilization_percent": 18.5,
      "cpu_memory_mb": 8192,
      "start_time": 1754210000000
    }
  ]
}
```

---

### `POST /v1/gpu/kill-process`
Terminates a runaway GPU compute process. Requires **ADMIN** or **OPERATOR** role with valid MFA TOTP verification.

#### Request Body
```json
{
  "gpu_id": "gpu-01",
  "pid": 28410,
  "signal": "SIGKILL",
  "mfa_token": "849201"
}
```

#### Response (`200 OK`)
```json
{
  "status": "SUCCESS",
  "message": "Process 28410 terminated successfully.",
  "audit_log_id": "audit-94821"
}
```

---

## 🤖 2. Gemini AI Integration API

The application interfaces directly with **Google Gemini Pro** (`v1beta/models/gemini-3.5-flash:generateContent`) for automated stack trace root-cause analysis and performance optimization recommendations.

### Privacy Redaction Pipeline
Prior to sending stack traces or log payloads to the Gemini API, all data is passed through `SecretRedactor` to strip sensitive information:
- **API Keys / Bearer Tokens**: Masked to `[REDACTED_API_KEY]`
- **IP Addresses**: Masked to `[REDACTED_IP]`
- **Internal Hostnames**: Masked to `[REDACTED_HOST]`

### Gemini Request Payload
```json
{
  "contents": [
    {
      "parts": [
        {
          "text": "Analyze the following PyTorch log and suggest the root cause and code fix:\n\ntorch.OutOfMemoryError: CUDA out of memory. Tried to allocate 12.00 GiB (GPU 0; 80.00 GiB total capacity; 68.20 GiB already allocated; 11.80 GiB free)."
        }
      ]
    }
  ],
  "systemInstruction": {
    "parts": [
      {
        "text": "You are a Principal AI Infrastructure & CUDA Engineer. Provide concise root cause analysis and actionable fixes."
      }
    ]
  }
}
```

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
