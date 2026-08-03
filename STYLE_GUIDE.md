# Kotlin Code Style Guide

## Naming
- Classes: PascalCase
- Functions/properties: camelCase
- Constants: SCREAMING_SNAKE_CASE
- Extension functions: descriptive verb (formatVramUsage())

## Compose Guidelines
- Composable functions: PascalCase
- Hoist state to ViewModel or parent composable
- Prefer collectAsStateWithLifecycle over collectAsState
- Use remember {} only for local ephemeral state

## Coroutines
- Use viewModelScope in ViewModels
- Never use GlobalScope in production
- Handle errors with .catch {} in flows
- Use runTest for coroutine unit tests

## Testing
- Unit tests for all use cases and ViewModels
- Mock with Mockito-Kotlin
- Room in-memory DB for integration tests
- Turbine for Flow testing
- Target >80% coverage on domain layer
