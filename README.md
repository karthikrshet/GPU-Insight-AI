
<div align="center">

# GPU Insight AI

### Open-Source AI-Powered GPU Monitoring & Infrastructure Companion for Android

Monitor • Diagnose • Optimize • Benchmark • Learn

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-API_26+-3DDC84.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/AI-Google_Gemini-orange.svg)](https://ai.google.dev/)
[![CI](https://github.com/karthikrshet/GPU-Insight-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/karthikrshet/GPU-Insight-AI/actions)
[![Open Source](https://img.shields.io/badge/Open_Source-Yes-success.svg)]()
[![Made With Love](https://img.shields.io/badge/Made_with-❤-red.svg)]()

**GPU Insight AI** is an open-source Android application designed for AI engineers, machine learning practitioners, DevOps teams, researchers, and students. It combines GPU monitoring, AI-assisted diagnostics, benchmarking, infrastructure insights, and educational tools into a modern mobile experience.

**Built with Kotlin • Jetpack Compose • Material 3 • Gemini AI • Room • Hilt • MVVM • WorkManager**

</div>

---

# Why GPU Insight AI?

Modern AI workloads depend heavily on GPUs, but understanding GPU performance, memory bottlenecks, CUDA issues, and infrastructure health often requires multiple tools.

GPU Insight AI brings monitoring, diagnostics, AI-powered explanations, and performance insights together in one Android application.

Whether you're training large language models, debugging CUDA issues, managing GPU infrastructure, or learning GPU architecture, GPU Insight AI aims to make complex GPU systems easier to understand.

---

# Key Features

## AI-Powered Diagnostics

- AI-assisted GPU performance analysis
- CUDA & PyTorch error explanation
- Out-of-memory (OOM) troubleshooting
- NCCL communication diagnostics
- TensorFlow & JAX log analysis
- Intelligent optimization recommendations

---

## GPU Monitoring

- Real-time GPU utilization
- VRAM usage monitoring
- Power consumption
- Temperature tracking
- Fan speed monitoring
- GPU clock frequencies
- Driver information
- Hardware health insights

---

## NVIDIA Infrastructure Features

- NVLink topology visualization
- Multi-GPU monitoring
- MIG partition visualization
- PCIe bandwidth monitoring
- ECC error detection
- Tensor Core utilization
- Triton Inference Server health checks
- GPU specification database

---

## Performance Analytics

- Historical metrics
- Interactive charts
- GPU utilization trends
- Thermal analysis
- Performance benchmarking
- Resource usage reports
- Performance scorecards

---

## AI Recommendations

Gemini AI provides guidance for:

- CUDA optimization
- Batch size recommendations
- Mixed precision (FP16/BF16)
- Memory optimization
- Performance bottleneck analysis
- Training optimization
- Inference optimization

---

## Security

- Privacy-first AI requests
- Sensitive data redaction
- Role-Based Access Control (RBAC)
- Audit logging
- Secure local storage
- Android Keystore integration

---

## Reports

Generate professional reports including:

- GPU health reports
- Performance summaries
- Benchmark reports
- Markdown export
- PDF export
- Executive summaries

---

## Learning Mode

Interactive educational content covering:

- GPU Architecture
- CUDA Fundamentals
- Tensor Cores
- VRAM Management
- PCIe & NVLink
- AI Infrastructure
- Performance Optimization

---

# Technology Stack

| Category | Technologies |
|----------|--------------|
| Language | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| Architecture | MVVM, Clean Architecture |
| Dependency Injection | Hilt |
| Local Storage | Room, DataStore |
| Networking | OkHttp |
| AI | Google Gemini API |
| Background Tasks | WorkManager |
| State Management | StateFlow |
| Build System | Gradle Kotlin DSL |
| CI/CD | GitHub Actions |

---

# Architecture

GPU Insight AI follows modern Android development practices.

```
Presentation Layer
│
├── Jetpack Compose
├── ViewModels
├── StateFlow
└── Navigation

↓

Domain Layer

Use Cases

Business Logic

Repository Interfaces

↓

Data Layer

Room

Network

DataStore

Repositories

↓

Infrastructure

Gemini API

Background Workers

Notifications

Storage
```

---

# Project Highlights

- Modern Android Architecture
- Clean Architecture
- MVVM
- Material Design 3
- Kotlin Coroutines
- StateFlow
- Hilt Dependency Injection
- Room Database
- WorkManager
- Google Gemini AI
- Modular Project Structure
- GitHub Actions CI/CD
- Apache 2.0 Licensed
- Open Source

---

# Getting Started

```bash
git clone https://github.com/karthikrshet/GPU-Insight-AI.git

cd GPU-Insight-AI

cp .env.example local.properties

# Add your Gemini API Key

./gradlew assembleDebug

./gradlew test
```

---

# Roadmap

- AI Diagnostics
- GPU Monitoring
- Performance Benchmarking
- NVIDIA GPU Support
- AI Recommendations
- Plugin System
- Desktop Companion
- Cloud Synchronization
- Wear OS Companion
- Compose Multiplatform

---

# Contributing

Contributions are welcome.

Please read:

- CONTRIBUTING.md
- CODE_OF_CONDUCT.md
- SECURITY.md

before submitting issues or pull requests.

---

# License

Licensed under the **Apache License 2.0**.

Copyright © 2026 **Karthik Rajesh Shet** (@karthikrshet)

---

<div align="center">

**⭐ If you find this project useful, consider giving it a star on GitHub.**

Building open-source tools for AI engineers, developers, and the GPU computing community.

</div>
