# Architecture Overview

GPU Insight AI follows Clean Architecture with MVVM:

## Layers
- **Presentation**: Jetpack Compose screens, ViewModels, StateFlow
- **Domain**: Use Cases, pure Kotlin business logic
- **Data**: Room DB, OkHttp client, DataStore

## Dependency Flow
UI -> ViewModel -> UseCase -> Repository -> DataSource

## Key Patterns
- Repository pattern for data abstraction
- Use Cases for single-responsibility business logic
- StateFlow for reactive UI updates
- Hilt for dependency injection
- SHA-256 hash chains for audit log integrity
