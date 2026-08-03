# GPU Insight AI — Release & Deployment Strategy

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🚀 1. Semantic Versioning (SemVer) & Release Cycles

GPU Insight AI strictly adheres to **Semantic Versioning 2.0.0** (`MAJOR.MINOR.PATCH`):
- **MAJOR (`v1.0.0 -> v2.0.0`):** Breaking architecture changes, database schema rewrites, or exporter protocol shifts.
- **MINOR (`v1.0.0 -> v1.1.0`):** New feature modules (e.g. gRPC streaming, adaptive layouts, new AI prompt templates).
- **PATCH (`v1.1.0 -> v1.1.1`):** Security patches, bug fixes, performance optimizations.

---

## 📦 2. Build Pipeline & Optimization Artifacts

### Baseline Profiles & Macrobenchmark
Every release build generates a **Baseline Profile** (`startup-prof.txt`) to pre-compile critical execution paths during installation, improving cold startup time by up to 40%:
```bash
./gradlew :app:generateBaselineProfile
```

### Proguard / R8 Shrinking Rules
Production APKs and AABs enable code shrinking and resource optimization in `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

---

## ⚙️ 3. CI/CD & Fastlane Automated Deployment

Every GitHub pull request and release tag triggers automated GitHub Actions workflows:

1. **Lint & Test Phase:** Runs Ktlint, Detekt, and unit/Robolectric test suite.
2. **Snapshot Verification:** Compares Roborazzi UI screenshots.
3. **Automated Signing:** Signs the release APK using secrets injected from GitHub Actions.
4. **Fastlane Supply:** Uploads release AABs directly to Google Play Store internal testing tracks.

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
