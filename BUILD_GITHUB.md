# Building JUNIOR Android on GitHub Actions

This repository uses GitHub Actions to build the JUNIOR Android application and produce a debug APK artifact.

## Workflow

The workflow is defined in `.github/workflows/android-build.yml` and triggers on:
- Push to `main` or `develop` branches
- Pull requests against `main`

It runs on `ubuntu-latest` GitHub runners, which provide proper x86_64 architecture for Android build tools.

## Build Environment

- **JDK**: Temurin 17 (OpenJDK 17)
- **AGP**: Android Gradle Plugin 8.9.1 (as defined in `gradle/libs.versions.toml`)
- **Gradle**: 8.11.1 (wrapper, as defined in `gradle/wrapper/gradle-wrapper.properties`)
- **Android SDK**: API level 34, Build Tools 34.0.0
- **Compile SDK**: 34 (project configuration in `app/build.gradle.kts`)
- **Target SDK**: 34
- **Min SDK**: 29

## How It Works

1. **Checkout** the repository
2. **Set up JDK 17** using `actions/setup-java`
3. **Accept Android SDK licenses** via `sdkmanager --licenses`
4. **Set up Android SDK** with API level 34 and Build Tools 34.0.0 using `android-actions/setup-android`
5. **Cache Gradle dependencies** to speed up subsequent builds
6. **Build the debug APK** using `./gradlew assembleDebug --no-daemon`
7. **Upload the APK** as a GitHub Actions artifact named `junior-debug-apk`

## APK Artifact

- **Name**: `junior-debug-apk`
- **Path**: `app/build/outputs/apk/debug/*.apk`
- Located in the GitHub Actions run artifacts after the workflow completes

## Downloading the APK

1. Go to the **Actions** tab in the GitHub repository
2. Click on the latest workflow run
3. Look for the **Artifacts** section (usually on the right side or bottom)
4. Download `junior-debug-apk`

## Required Repository Settings

No special settings are required. The workflow uses:
- Default `ubuntu-latest` runner
- Standard Android SDK setup
- Gradle wrapper from the repository

## Notes

- The workflow does **not** require any secrets or API keys
- Release signing is not configured; the debug APK is unsigned
- To build a release APK, additional signing configuration would be needed
- The `android-actions/setup-android` action handles SDK platform and build tools installation