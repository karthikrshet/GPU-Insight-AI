# Database Schema

## Tables

### gpu_metrics
| Column             | Type    | Notes                     |
|--------------------|---------|---------------------------|
| id                 | INTEGER | Auto PK                   |
| timestamp          | INTEGER | Unix epoch ms             |
| gpuId              | INTEGER | GPU index                 |
| gpuName            | TEXT    | GPU model name            |
| utilizationPercent | REAL    | 0.0–100.0                 |
| vramUsedMb         | INTEGER | VRAM used MB              |
| vramTotalMb        | INTEGER | VRAM capacity MB          |
| powerDrawWatts     | REAL    | TDP in watts              |
| temperatureCelsius | REAL    | Junction temp °C          |
| clockFrequencyMhz  | INTEGER | Core clock MHz            |
| fanSpeedPercent    | REAL    | Fan %                     |

### audit_events
| Column       | Type    | Notes                        |
|--------------|---------|------------------------------|
| id           | INTEGER | Auto PK                      |
| timestamp    | INTEGER | Unix epoch ms                |
| eventType    | TEXT    | LOGIN, CONFIG_CHANGE, etc.   |
| userId       | TEXT    | User identifier              |
| action       | TEXT    | Action performed             |
| previousHash | TEXT    | SHA-256 of previous event    |
| currentHash  | TEXT    | SHA-256 of this event        |
