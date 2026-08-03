# GPU Insight AI — Engineering Style Guide & Best Practices

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🎨 1. Kotlin Code Conventions

1. **Language Standard:** Kotlin 1.9 / 2.0+ with explicit type declarations on public APIs.
2. **Immutability First:** Prefer `val` over `var`. Use immutable data classes (`@Immutable` / `@Stable`) for Jetpack Compose state models.
3. **Coroutines & Flow:** Always specify explicit `CoroutineDispatcher` (e.g. `Dispatchers.IO` for DB/Network, `Dispatchers.Default` for heavy calculations).
4. **Error Handling:** Return `Result<T>` from repositories and UseCases. Avoid swallowing exceptions without logging.

---

## 📱 2. Jetpack Compose Guidelines

### State Hoisting & UDF
- Composables must be stateless whenever possible. Pass state down as parameters and events up as lambdas.
```kotlin
@Composable
fun GpuMetricsCard(
    gpu: GpuEntity,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
)
```

### TestTags Required on Interactive Elements
All interactive components (Buttons, Chips, TextFields, Cards) **MUST** include a unique `testTag` modifier for automated Compose UI testing and Roborazzi snapshot verification:
```kotlin
Button(
    onClick = { onAction() },
    modifier = Modifier.testTag("action_button_tag")
)
```

### Dynamic Sizing & Ellipsis
Always enforce text overflow safety to prevent text truncation on smaller devices:
```kotlin
Text(
    text = title,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
```

---

## 🛡️ 3. Architecture Boundary Rules

1. **No Android UI in Domain/Data:** Domain UseCases and Data Repositories must not import Android framework packages (`android.os.*`, `androidx.compose.*`).
2. **Feature Isolation:** Feature modules must never depend directly on other feature modules. All communication flows through `:core:data`, `:core:domain`, or Navigation routes.

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
