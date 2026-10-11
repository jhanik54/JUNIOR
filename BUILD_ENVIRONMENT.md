# JUNIOR Android Build Environment Setup

## Environment Status: PARTIALLY CONFIGURED (Network Limited)

This document records the build environment setup for the JUNIOR Android project on Termux (Android 12, arm64-v8a).

---

## ✅ Successfully Configured

### Java/JDK
- **Version**: OpenJDK 17.0.20
- **Location**: `/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk`
- **JAVA_HOME**: `/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk`
- **Status**: ✅ Working (`java -version` and `javac -version` confirmed)

### Android Tools (Termux Packages)
- **aapt2**: 16.0.0.4-2 ✅
- **aidl**: 16.0.0.4-2 ✅
- **android-tools** (adb, fastboot): 37.0.0-2 ✅
- **Location**: `/data/data/com.termux/files/usr/bin/`

### Project Configuration
- **Package**: `ai.affiora.junior` (renamed from `ai.affiora.mobileclaw`)
- **AGP**: 8.10.0
- **Kotlin**: 2.2.0
- **KSP**: 2.2.0-2.0.2
- **Gradle**: 8.11.1 (wrapper)
- **compileSdk**: 36 (Android 14)
- **targetSdk**: 34
- **minSdk**: 29
- **Java Compatibility**: 17

### License Files (Manual)
- **android-sdk-license**: Created at `/data/data/com.termux/files/home/android-sdk/licenses/android-sdk-license`
- Contains standard Google Android SDK license hashes

### Environment Variables (Persisted in ~/.bashrc)
```bash
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=/data/data/com.termux/files/home/android-sdk
export ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$JAVA_HOME/bin
```

---

## ❌ Blocked by Network Limitations

### Android SDK Command-Line Tools
- **Required**: `sdkmanager`, `avdmanager`, and associated JAR libraries
- **Issue**: All downloads from `dl.google.com/android/repository/` result in corrupted/incomplete ZIP files
- **Attempted**: Multiple URLs, versions (11076708, 10406996), tools (wget, curl, 7z)
- **Result**: Consistent "Unexpected end of archive" and CRC failures
- **Root Cause**: Network connectivity issues between Termux environment and Google's servers

### Gradle Distribution
- **Required**: gradle-8.11.1-bin.zip (~150MB)
- **Issue**: Download times out (10s default timeout, extremely slow speeds ~50KB/s)
- **Partial Cache**: 1.2MB downloaded at `/data/data/com.termux/files/home/.gradle/wrapper/dists/gradle-8.11.1-bin/`

### Android Platform SDK & Build Tools
- **Required**: android-36 (compileSdk), build-tools 34.0.0
- **Blocked**: Cannot install without working `sdkmanager`

---

## 📋 Required Versions (Per Project Config)

| Component | Version | Source |
|-----------|---------|--------|
| AGP | 8.10.0 | gradle/libs.versions.toml |
| Kotlin | 2.2.0 | gradle/libs.versions.toml |
| KSP | 2.2.0-2.0.2 | gradle/libs.versions.toml |
| Gradle | 8.11.1 | gradle/wrapper/gradle-wrapper.properties |
| compileSdk | 36 (Android 14) | app/build.gradle.kts |
| targetSdk | 34 | app/build.gradle.kts |
| minSdk | 29 | app/build.gradle.kts |
| Java | 17 | app/build.gradle.kts (compileOptions) |
| NDK | Not required | - |

---

## 🛠 Setup Commands (For Future Sessions)

```bash
# 1. Set environment variables
export JAVA_HOME=/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=/data/data/com.termux/files/home/android-sdk
export ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$JAVA_HOME/bin

# 2. Verify Java
java -version && javac -version

# 3. Verify Android tools
aapt2 version && aidl --version && adb version

# 4. Attempt build (will fail without SDK components)
cd /data/data/com.termux/files/home/MobileClaw
./gradlew assembleDebug
```

---

## 🚫 Current Build Status

### `./gradlew assembleDebug` Result: **CANNOT RUN**

**Reason**: Network connectivity prevents downloading:
1. Gradle 8.11.1 distribution
2. Android SDK command-line tools (sdkmanager)
3. Android platform SDK (android-36)
4. Android build-tools (34.0.0)

**Error**: `java.net.SocketTimeoutException: Read timed out` on all external downloads.

---

## 💡 Recommended Solutions (Outside This Environment)

1. **Use a machine with stable internet** (Linux/macOS/Windows) with:
   - JDK 17
   - Android SDK (via Android Studio or commandlinetools)
   - Run `./gradlew assembleDebug` normally

2. **Pre-download dependencies** on a connected machine:
   - Gradle 8.11.1 distribution
   - Android SDK command-line tools (11076708)
   - android-36 platform
   - build-tools 34.0.0
   - Copy to Termux via adb/scp

3. **Use GitHub Actions / CI** for building APKs

4. **Local Maven Mirror**: Set up a local Maven/Android SDK mirror on LAN

---

## 📁 Key Paths

| Component | Path |
|-----------|------|
| Project Root | `/data/data/com.termux/files/home/MobileClaw` |
| JDK 17 | `/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk` |
| Android SDK | `/data/data/com.termux/files/home/android-sdk` |
| License File | `/data/data/com.termux/files/home/android-sdk/licenses/android-sdk-license` |
| Gradle Cache | `/data/data/com.termux/files/home/.gradle` |
| Termux Packages | `/data/data/com.termux/files/usr/bin/` |

---

## 📝 Notes

- All JUNIOR source code rebranding (MobileClaw → JUNIOR) is complete
- Architecture improvements (TaskState, Planner, ExecutionPipeline, MemoryInterfaces) are implemented
- Zero code changes needed for build - only environment setup
- Network is the sole blocker in this Termux environment
