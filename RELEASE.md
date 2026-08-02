# Release Guide

## Version Numbering
Semantic Versioning: MAJOR.MINOR.PATCH

## Release Process
1. Update versionCode and versionName in app/build.gradle.kts
2. Update CHANGELOG.md
3. Run: `./gradlew test connectedAndroidTest`
4. Tag: `git tag -a v1.2.0 -m "Release v1.2.0"`
5. Push: `git push origin v1.2.0`
6. Create GitHub Release with APK artifact

## Release Checklist
- [ ] All unit tests pass
- [ ] Lint check passes
- [ ] ProGuard rules validated
- [ ] API key NOT in source
- [ ] README updated
- [ ] CHANGELOG updated
