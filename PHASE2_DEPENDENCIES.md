# JUNIOR Phase 2 Dependencies Plan

## Existing Dependencies (Phase 1 - FROZEN)

All Phase 1 dependencies remain unchanged. No version bumps, no removals.

### Core AndroidX
```
agp = "8.9.1"           # Android Gradle Plugin - Phase 1
kotlin = "2.2.0"        # Kotlin compiler - Phase 1
compose-bom = "2025.03.00"  # Compose BOM - Phase 1 (updated to latest)
hilt = "2.57.2"         # Dagger-Hilt - Phase 1
room = "2.6.1"          # Room database - Phase 1
ktor = "3.4.2"          # HTTP client - Phase 1
okhttp = "4.12.0"       # OKHTTP - Phase 1
coroutines = "1.9.0"    # Coroutines - Phase 1
lifecycle = "2.8.7"     # Lifecycle - Phase 1
```

### Phase 1 Verified Dependencies (135+ files, builds on GitHub Actions)
- All existing tools, skills, memory, pipeline code works with these versions
- Do NOT change unless a real build error requires it

---

## New Phase 2 Dependencies

Add these to `gradle/libs.versions.toml` under `[versions]` section:

### TensorFlow Lite (On-Device Embeddings)
```
tflite = "2.16.0"                          # TensorFlow Lite core
litertlm = "0.10.0"                        # LiteRT-LM (already exists, verify)
litertlm-android = { group = "com.google.ai.edge.litertlm", name = "litertlm-android", version.ref = "litertlm" }
```

### Google Play Services Vision (OCR)
```
play-services-vision = "21.1.0"           # OCR and vision APIs
```

### Camera X View (UI preview for OCR/camera)
```
camera-view = "1.1.0"                      # androidx.camera:camera-view
```

### WorkManager (Long-running tasks)
```
work-runtime = "2.9.0"                     # androidx.work:work-runtime-ktx (update from 2.11.2)
lifecycle-viewmodel = "2.7.0"              # androidx.lifecycle:lifecycle-viewmodel-ktx (update from 2.8.7)
```

### Compose BOM Update
```
compose-bom = "2024.11.00"                 # androidx.compose:compose-bom
# Note: Phase 1 has 2025.03.00, may need to adjust if 2024.11.00 is more stable
```

### Optional / Feature-Gated Dependencies

These are added conditionally ( behind `if (phase == 2) ` configuration or feature flags):

#### Tesseract OCR Native (fallback if play-services-vision unavailable)
```
tesseract = "5.0.0-alpha"                  # Native C++ OCR (optional)
```

#### gRPC (remote agent coordination)
```
grpc-kotlin = "1.60.0"                     # org.grpc:grpc-kotlin-stub (optional)
```

#### Firebase App Distribution (skill updates)
```
firebase-appdistro = "2.2.0"               # Firebase App Distribution API (optional)
```

#### Permission Management Library (optional)
```
acrt-permissions = "1.0.0"                 # com.google.acrt:acrt-permissions (optional)
```

---

## Dependency Version Catalog Entries

Add these to `[libraries]` section in `libs.versions.toml`:

### TensorFlow Lite
```toml
# On-device embeddings for RAG
tflite-android = { group = "com.google.ai.edge.litertlm", name = "litertlm-android", version.ref = "litertlm" }
```

### Google Vision
```toml
# OCR and visual grounding
vision-play-services = { group = "com.google.android.gms", name = "play-services-vision", version = "21.1.0" }
```

### Camera View
```toml
# Camera preview for OCR
camera-view = { group = "androidx.camera", name = "camera-view", version = "1.1.0" }
```

### WorkManager
```toml
# Long-running task management
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version = "2.9.0" }
lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version = "2.7.0" }
```

### Compose (if not using BOM reference)
```toml
compose-ui = { group = "androidx.compose.ui", name = "ui", version = "1.6.0" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version = "1.3.0" }
```

---

## Dependency Risk Assessment

| Dependency | Category | Risk Level | Mitigation |
|-----------|----------|-----------|-----------|
| `tflite-android` | On-device embeddings | Low | Bundled with app, no network needed. Small model footprint (~5MB). |
| `play-services-vision` | OCR | Medium | Google Play Services required. Fall back to Tesseract native if unavailable. |
| `camera-view` | Camera preview | Low | AndroidX, well-maintained. Optional; use default CameraX if unavailable. |
| `work-runtime-ktx` | Long tasks | Low | AndroidX stable, replaces work-runtime 2.11.2. |
| `lifecycle-viewmodel-ktx` | UI lifecycle | Low | AndroidX stable, replaces lifecycle 2.8.7. |
| `compose-bom` | UI toolkit | Low-Medium | Compose BOM version bump. Test with existing Compose version. |
| `vision-play-services` | OCR system | Medium | Requires Google Play services installed. Most modern Android devices have it. |
| `tesseract-native` | Fallback OCR | Low | C++ native library. Only include if play-services-vision fails. |

---

## Gradle Configuration Changes

### `build.gradle.kts` (app level) - NEW additions

Add to `plugins` block (if not already present):
```kotlin
plugins {
    // Phase 1 (existing)
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

    // Phase 2 optional plugins
    // "org.tensorflow.lite.lint"  // Uncomment if using TFLint
}
```

### Dependency declarations (in `dependencies` block)

```kotlin
// Phase 1 (existing - DO NOT MODIFY)
implementation(platform("androidx.compose:compose-bom"))
implementation("androidx.core:core-ktx")
implementation("androidx.lifecycle:lifecycle-runtime-ktx")
implementation("com.google.dagger:hilt-android")
kapt("com.google.dagger:hilt-compiler")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
implementation("androidx.work:work-runtime-ktx:2.11.2")  // Phase 1
implementation("androidx.room:room-runtime:2.6.1")
implementation("com.anthropic:anthropic-java:2.19.0")
implementation("com.google.ai.edge.litertlm:litertlm-android:0.10.0")

// Phase 2 NEW dependencies
// On-device embeddings for RAG (Memory enhancement)
implementation("com.google.ai.edge.litertlm:litertlm-android:2.16.0")

// OCR and visual grounding
implementation("com.google.android.gms:play-services-vision:21.1.0")

// Camera preview for OCR/camera interactions
implementation("androidx.camera:camera-view:1.1.0")

// Long-running autonomous task management
implementation("androidx.work:work-runtime-ktx:2.9.0")  // updated from 2.11.2
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")  // updated from 2.8.7

// Compose UI components (if not using BOM)
implementation("androidx.compose.ui:ui:1.6.0")
implementation("androidx.compose.material3:material3:1.3.0")
```

---

## Version Compatibility Matrix

| Dependency | Minimum | Recommended | Maximum tested |
|-----------|---------|-------------|----------------|
| AGP | 8.9.1 | 8.9.1 | 8.9.1 (Phase 1) |
| Kotlin | 2.2.0 | 2.2.0 | 2.2.0 (Phase 1) |
| Compose BOM | 2024.11.00 | 2025.03.00 | 2025.03.00 (Phase 1) |
| Hilt | 2.57.2 | 2.57.2 | 2.57.2 (Phase 1) |
| Room | 2.6.1 | 2.6.1 | 2.6.1 (Phase 1) |
| TFLite | 2.16.0 | 2.16.0 | N/A (Phase 2 new) |
| Play Services Vision | 21.1.0 | 21.1.0 | N/A (Phase 2 new) |
| Camera View | 1.1.0 | 1.1.0 | N/A (Phase 2 new) |
| WorkManager | 2.11.2 | 2.9.0 | 2.11.2 (Phase 1), 2.9.0 (Phase 2) |
| Lifecycle | 2.8.7 | 2.7.0 | 2.8.7 (Phase 1), 2.7.0 (Phase 2) |
| Kotlin Coroutines | 1.9.0 | 1.9.0 | 1.9.0 (Phase 1) |

---

## Dependency Installation Verification

```bash
# Verify Phase 1 still builds (FROZEN)
./gradlew :app:assembleDebug --no-daemon

# Verify Phase 2 dependencies can resolve
./gradlew :app:dependencies --no-daemon 2>&1 | grep -E "(tflite|play-services|camera-view|work-runtime|lifecycle-viewmodel)"

# Check for version conflicts
./gradlew :app:dependencies --no-daemon 2>&1 | grep -E "Can't choose" || echo "No version conflicts found"

# Build with Phase 2 flags (if using custom build logic)
./gradlew :app:assembleDebug -Pphase=2 --no-daemon
```

---

## Critical: Do NOT Modify These Phase 1 Dependencies

| Dependency | Reason NOT to change |
|-----------|---------------------|
| `agp = "8.9.1"` | Phase 1 builds successfully; changing risks breaking build |
| `kotlin = "2.2.0"` | Android Stuido integration; changing risks compilation errors |
| `compose-bom = "2025.03.00"` | Compose UI foundation; changing risks UI breakage |
| `hilt = "2.57.2"` | DI framework for entire app; changing risks runtime errors |
| `room = "2.6.1"` | Database layer; changing risks schema migration errors |
| `ktor = "3.4.2"` | HTTP client for AI providers; changing risks API failures |
| `okhttp = "4.12.0"` | Underlying HTTP transport; changing risks network failures |
| `coroutines = "1.9.0"` | Concurrency foundation; changing risks coroutine leaks |
| `lifecycle = "2.8.7"` | Component lifecycle; changing risks leaks/failures |
| `ktor-client-okhttp` | Ktor HTTP integration; changing risks HTTP failures |

**Only add NEW dependencies for Phase 2 features. Never bump or change Phase 1 versions.**