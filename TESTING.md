# Testing Guide

## Unit Tests
```bash
./gradlew test
```
Location: `app/src/test/`

### Coverage
- Repository: DAO delegation, data pruning
- Use Cases: business logic isolation  
- ViewModel: state management, coroutines

## Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Test Libraries
- JUnit 4 + Mockito
- Kotlin Coroutines Test + Turbine
- Room in-memory database for integration tests
