# GPU Insight AI — Database & Persistence Specification

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🗄️ 1. Local Offline Room Database Schema

GPU Insight AI uses **Android Room ORM** (backed by **SQLite / SQLCipher**) for offline caching of telemetry time-series metrics, cluster node metadata, security audit logs, thermal alert rules, and generated executive reports.

```
┌───────────────────────────┐         1:N         ┌───────────────────────────┐
│     ClusterNodeEntity     ├────────────────────►│         GpuEntity         │
└───────────────────────────┘                     └─────────────┬─────────────┘
                                                                │ 1:N
                                                                ▼
                                                  ┌───────────────────────────┐
                                                  │   MetricTelemetryEntity   │
                                                  └───────────────────────────┘
```

---

## 📐 2. Entity Definitions & Indexes

### `ClusterNodeEntity`
- `id` (String, PK): Node ID (e.g. `node-nv-01`).
- `clusterName` (String): Parent cluster name.
- `ipAddress` (String): Host IP address.
- `status` (String): `ONLINE`, `OFFLINE`, `DEGRADED`.

### `GpuEntity`
- `id` (String, PK): GPU UUID.
- `nodeOwnerId` (String, FK -> `ClusterNodeEntity.id`): Parent node ID.
- `name` (String): Hardware name (e.g. `NVIDIA H100 SXM5 80GB`).
- `vendor` (String): `NVIDIA`, `AMD`, `Intel`.
- Indexed on `nodeOwnerId` for fast cluster join queries.

### `MetricTelemetryEntity`
- `id` (Long, Auto-PK): Unique sample identifier.
- `gpuId` (String, Indexed): Target GPU UUID.
- `timestamp` (Long, Indexed): Epoch timestamp.
- `temperatureGpu` (Float), `vramUsedMb` (Int), `powerDrawWatts` (Float), `smClockMhz` (Int).
- **Index:** Composite index on `(gpuId, timestamp DESC)` for fast chart window retrieval.

### `AuditLogEntity`
- `id` (String, PK): Unique audit GUID.
- `timestamp` (Long): Timestamp of action.
- `actorRole` (String): `OWNER`, `ADMIN`, `OPERATOR`, `AUDITOR`.
- `action` (String): Operation description (e.g. `PROCESS_KILL_SIGKILL`).
- `targetResource` (String): Target PID or rule ID.

---

## 🔒 3. Encryption & Data Retention Policy

1. **SQLCipher Table Encryption:** Sensitivity tables containing audit logs and user tokens are encrypted using SQLCipher with a key secured in Android Keystore.
2. **Automated Cache Eviction:** Telemetry metrics older than 30 days are automatically purged via a periodic Room maintenance transaction triggered by `WorkManager`.

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
