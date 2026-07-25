# Changelog

## [Unreleased]

## [1.2.0] - 2026-04-20
### Added
- Tensor parallelism advisor for LLM deployment
- InfiniBand link health monitoring
- VRAM defragmentation advisor
- Memory leak detector
- FLOPs counter for training profiling
- Fan curve optimizer (silent/performance modes)
- CUDA/PyTorch compatibility checker
- Triton Inference Server health checker
- GitHub Actions CI workflow

### Changed
- Upgraded to Hilt DI throughout all layers
- versionCode=3, versionName=1.2.0

## [1.1.0] - 2026-01-19
### Added
- MIG partition tracking for H100/A100
- PCIe Gen4/5 bandwidth monitoring
- ECC error rate assessment
- Power efficiency (TFLOPS/W) analysis
- GPU spec database (H100, A100, L40S, RTX 4090, H200)
- Prometheus metrics scraper
- ProtoDataStore for user preferences
- OOM error parser (PyTorch/CUDA/NCCL)

## [1.0.0] - 2025-09-02
### Added
- Initial project with Jetpack Compose
- Room DB with GPU metrics and audit events
- Gemini AI error analysis
- WorkManager thermal daemon
- RBAC 5-tier role hierarchy
- SHA-256 audit log hash chains
- NVLink topology visualizer
- Carbon footprint calculator
- Chaos Engineering simulator
