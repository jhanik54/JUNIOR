# JUNIOR Phase 2 Architecture Plan

## Overview

Phase 2 extends the JUNIOR Phase 1 foundation with advanced capabilities while preserving all existing working functionality. The architecture is designed incrementally, adding new components via clean interfaces that can be tested and swapped independently.

## Package Structure Updates

```
ai.affiora.junior
├── agent/              # Core agent runtime (Phase 1 foundation)
│   ├── AgentRuntime.kt       # Main agent execution loop (Phase 1)
│   ├── AgentService.kt       # Foreground service for background operation (Phase 1)
│   ├── AiProvider.kt         # AI provider definitions (Phase 1)
│   ├── MemoryStore.kt        # File-based memory implementation (Phase 1)
│   ├── MemoryInterfaces.kt   # Clean interfaces for memory (Phase 1)
│   ├── Planner.kt            # Planning abstraction (Phase 1)
│   ├── ExecutionPipeline.kt  # Structured pipeline stages (Phase 1)
│   ├── TaskState.kt          # Task lifecycle states (Phase 1)
│   ├── SystemPromptBuilder.kt# System prompt construction (Phase 1)
│   ├── ClaudeApiClient.kt    # AI API client with failover (Phase 1)
│   ├── LocalInferenceEngine.kt # On-device inference (Phase 1)
│   ├── LocalModelManager.kt  # Local model management (Phase 1)
│   ├── LocalToolAdapter.kt   # Tool adapter for local inference (Phase 1)
│   ├── PermissionManager.kt  # Tool permission management (Phase 1)
│   ├── RetryPolicy.kt        # Cross-provider failover policy (Phase 1)
│   ├── ScheduleEngine.kt     # Recurring task scheduling (Phase 1)
│   ├── ContextCompactor.kt   # Conversation compaction (Phase 1)
│   ├── AgentRuntimeMetrics.kt # NEW: metrics and monitoring
│   ├── BrowserAgent.kt       # NEW: advanced browser automation
│   ├── OCRGrounding.kt       # NEW: OCR + visual grounding
│   ├── MultiAgentCoordinator.kt # NEW: multi-agent worker architecture
│   ├── CodeSandbox.kt        # NEW: secure code execution sandbox
│   ├── SkillsManager.kt      # REFACTORED: advanced tools/skills system
│   ├── VectorMemory.kt       # NEW: RAG-enabled memory
│   ├── AgentConsole.kt       # NEW: agent console UI
│   ├── PauseResumeController.kt # NEW: pause/resume/stop controls
│   ├── LongRunningTaskManager.kt # NEW: long-running autonomous execution
│   ├── ConfirmationManager.kt # REFACTORED: advanced confirmation/permissions
│   └── backend/              # AI provider backends (Phase 1)
├── channels/           # Messaging channels (Phase 1)
├── connectors/         # OAuth connectors (Phase 1)
├── data/               # Data layer (Phase 1)
│   ├── db/                 # Room database (enhanced for Phase 2)
│   ├── model/              # Updated data models
│   └── prefs/              # User preferences (DataStore) (Phase 1)
├── di/                 # Dependency injection (Hilt) (Phase 1)
├── gateway/            # Firebase messaging (Phase 3, optional)
├── plugins/            # NEW: Plugin system
├── skills/             # REFACTORED: Advanced skills management
├── tools/              # Android tools (Phase 1, expanded)
├── ui/                 # Compose UI (Phase 1, enhanced)
│   ├── chat/               # Chat screen (Phase 1)
│   ├── cron/               # Scheduling screen (Phase 1)
│   ├── devices/            # Device pairing (Phase 3)
│   ├── onboarding/         # First-run experience (Phase 1)
│   ├── settings/           # Settings screens (Phase 1, extended)
│   ├── skills/             # Skills library (Phase 1, expanded)
│   ├── theme/              # Material3 theme (Phase 1)
│   └── console/            # NEW: Agent console UI
└── util/               # Utilities (Phase 1, extended)
```

## Phase 2 Feature Architecture

### 1. Advanced Browser Agent

**Existing Foundation:**
- WebBrowserTool.kt (298 lines) - Basic web interaction
- HttpTool.kt (132 lines) - HTTP requests
- Existing tool infrastructure

**Missing Components:**
- BrowserAgent.kt - Orchestrates browser automation with step planning
- Visual grounding integration
- Playwright-like DOM interaction layer (via existing WebBrowserTool)
- Browser session management with checkpoint support

**Android-Specific Requirements:**
- WebView with JavaScript interface for file download/upload
- Permission handling (storage, internet, runtime permissions)
- Background tab management

**Required Dependencies:**
- `androidx.webkit:webkit:1.8.0` (system WebView)
- Optional: `com.github.kittinunf.fuel:fuel:6.0.0` for HTTP client

**Security Risks:**
- Uncontrolled navigation to malicious URLs
- JavaScript injection vulnerabilities
- File system access from web context
- Browser history tracking

**Confirmation Required:**
- Navigating to external URLs
- File download locations
- JavaScript execution permissions

### 2. OCR + Visual/UI Grounding

**Existing Foundation:**
- ScreenCaptureTool.kt - Screen capture
- UIAutomationTool.kt - UI interaction
- Vision/AI provider integration via ClaudeApiClient

**Missing Components:**
- OCRGrounding.kt - OCR + UI element grounding
- Visual element detection and localization
- Cross-app UI state tracking

**Android-Specific Requirements:**
- `androidx.camera:camera-view` for camera-based OCR
- `com.google.android.gms:play-services-vision` for OCR
- Accessibility service for UI element tree
- Overlay windows for real-time grounding

**Required Dependencies:**
- `com.google.android.gms:play-services-vision:21.1.0`
- `androidx.camera:camera-view:1.1.0`
- Optional: Tesseract OCR native library

**Security Risights:**
- OCR data leakage (images sent to external APIs)
- Accessibility service abuse
- Overlay window permission misuse

**Confirmation Required:**
- OCR enabling (first use)
- Accessibility service activation
- Image capture permissions

### 3. Multi-Agent Worker Architecture

**Existing Foundation:**
- AgentRuntime - Single agent loop
- ExecutionPipeline - Pipeline stages
- Skill system - Built-in skills

**Missing Components:**
- MultiAgentCoordinator - Manages agent swarm/team
- WorkerAgent - Worker agent type with role/identity
- AgentRegistry - Register/discover agents
- Task decomposition across agents
- Consensus/coordination protocol

**Android-Specific Requirements:**
- AIDL or Messenger for inter-process communication (if separate processes)
- SharedPreferences or Room for agent state sync
- Notification for agent status changes

**Required Dependencies:**
- None native (pure Kotlin architecture)
- Optional: gRPC for remote agent coordination

**Security Risks:**
- Agent identity spoofing
- Task duplication across agents
- Resource contention (multiple agents same tool)
- Communication channel hijacking

**Confirmation Required:**
- Creating new agent instances
- Cross-agent task delegation
- Agent network participation

### 4. Code Execution with Secure Sandbox/Isolation

**Existing Foundation:**
- No code execution capability currently
- HttpTool for remote execution
- Skill system for tool orchestration

**Missing Components:**
- CodeSandbox.kt - Secure execution environment
- Kotlin/Java script execution with timeout
- Output capture and result handling
- Sandbox isolation from system resources

**Android-Specific Requirements:**
- `com.github.turansky.kfc.plugin:kfc-gradle-plugin` for Kotlin/JS
- Android `ProcessBuilder` with restricted syscalls
- Separate process with UID isolation
- File descriptor limiting

**Required Dependencies:**
- None (Android ProcessBuilder native)
- Optional: `org.jetbrains.kotlin:kotlin-scripting` for script execution
- Optional: `jshell`-like REPL implementation

**Security Risks:**
- Arbitrary code execution escape
- System resource exhaustion
- File system access from sandbox
- Network access from sandboxed code
- Memory corruption attacks

**Confirmation Required:**
- First-time code execution enablement
- Sandbox permission grants
- Output review before display

### 5. Advanced Tools/Skills System

**Existing Foundation:**
- 30+ tools in tools/
- Built-in skills
- SkillAuthorTool.kt (237 lines)

**Missing Components:**
- SkillsManager.kt - Centralized skills management
- User-installed skills from external sources
- Skill dependency resolution
- Skill versioning and updates
- Skill marketplace integration
- AI-assisted skill adaptation

**Android-Specific Requirements:**
- APK signing verification for installed skills
- Split APK support
- Feature module delivery

**Required Dependencies:**
- None native (existing tool system)
- Optional: Gradle plugin for skill packaging
- Optional: Firebase App Distribution for skill updates

**Security Risks:**
- Malicious skill installation
- Skill permission escalation
- Skill-to-skill attacks
- Unsigned skill execution

**Confirmation Required:**
- Installing user-installed skills
- Granting skill permissions
- Skill marketplace participation

### 6. Advanced Memory/RAG

**Existing Foundation:**
- MemoryStore.kt - File-based memory (Phase 1)
- SemanticMemoryStore interface (Phase 1)
- MemoryContextBuilder (Phase 1)
- Durable facts, daily notes, dreams diary

**Missing Components:**
- VectorMemory.kt - Vector-based memory with RAG
- Embedding model integration (on-device or API)
- Semantic search with similarity scoring
- Memory pruning and forgetting curve
- Cross-session context retention

**Android-Specific Requirements:**
- On-device embedding model (small footprint)
- `org.tensorflow:tensorflow-lite` for inference
- MMAP for vector database storage
- SQLite FTS3/FTS4 for text index

**Required Dependencies:**
- `org.tensorflow:tensorflow-lite:2.16.0`
- `org.tensorflow:tensorflow-lite-gpu:2.16.0` (optional)
- `org.tensorflow:tensorflow-lite-selectbuf:2.16.0`
- Optional: `com.github.ethan-research:sentence-transformers` for embeddings

**Security Risks:**
- Embedding model supply chain attack
- Vector database corruption
- Privacy leakage in memory contents
- Cross-user memory contamination

**Confirmation Required:**
- First vector index build
- Model download confirmation
- Memory pruning enablement

### 7. Agent Console

**Existing Foundation:**
- Compose UI screens (chat, settings, skills)
- AgentRuntime events

**Missing Components:**
- AgentConsole - Standalone console UI
- Real-time agent state display
- Live tool execution log
- Performance metrics Dashboard
- Command palette for quick actions

**Android-Specific Requirements:**
- Compose Material3 themed UI
- RecyclerView for scrollable logs
- Pull-to-refresh
- Searchable filter

**Required Dependencies:**
- `androidx.compose:compose-bom:2024.01.00`
- `androidx.compose.ui:ui:2024.01.00`
- `androidx.compose.material3:material3:1.2.0`

**Security Risks:**
- UI injection attacks
- Credential exposure in logs
- Log data leakage

**Confirmation Required:**
- Console access permission
- Log level configuration

### 8. Pause/Resume/Stop Controls

**Existing Foundation:**
- TaskState enum with PAUSED, WAITING states
- ExecutionPipeline CONTINUE stage
- AgentRuntime confirmAction

**Missing Components:**
- PauseResumeController - Unified pause/resume/stop
- StopConfirmationDialog - Safe stop with cleanup
- Pause persistence across app restarts
- Hotkey/gesture shortcuts

**Android-Specific Requirements:**
- Foreground service lifecycle management
- Notification for pause state
- App widget for quick controls
- Back button handling for pause

**Required Dependencies:**
- None native (existing service architecture)
- Optional: WorkManager for persistent pauses
- Optional: NotificationCompat for enhanced UI

**Security Risks:**
- Unintentional task termination
- State corruption on forced stop
- Resource leak on pause/resume

**Confirmation Required:**
- Stop task confirmation (destructive action)
- Pause resume confirmation (context preservation)
- Emergency stop bypass

### 9. Long-Running Autonomous Task Execution

**Existing Foundation:**
- ExecutionPipeline with checkpoint/resume
- ScheduleEngine (recurring tasks)
- AgentRuntime with runWithHistory

**Missing Components:**
- LongRunningTaskManager - Autonomous execution orchestrator
- Step timeout handling
- Progress reporting
- Automatic checkpoint at configurable intervals
- Break/resume from interrupted state

**Android-Specific Requirements:**
- Foreground service with persistent notification
- WorkManager integration for constrained tasks
- Battery optimization exemptions
- Memory pressure handling

**Required Dependencies:**
- `androidx.work:work-runtime:2.9.0`
- `androidx.lifecycle:lifecycle-viewmodel:2.7.0`
- Optional: `com.neen80:reloader:2.0.0` for reflection

**Security Risks:**
- Unbounded task execution
- Resource exhaustion from long runs
- State drift without checkpoints
- Unable to terminate long tasks

**Confirmation Required:**
- Autonomous mode enablement
- Long task timeout configuration
- Battery optimization exemption grant

### 10. Security, Permissions and Confirmation System

**Existing Foundation:**
- PermissionManager.kt (Phase 1)
- AgentRuntime confirmAction
- Built-in confirmation dialogs
- Skill security scanning

**Missing Components:**
- AdvancedConfirmationManager - Granular confirmation workflow
- Permission request flow with rationale
- Permission memory (remember/never ask)
- Security audit logging
- Confirmed action history

**Android-Specific Requirements:**
- Permissions registry
- Runtime permission callback handling
- ShouldShowRequestPermissionRationale flows
- Permission groups management

**Required Dependencies:**
- None native (AndroidX permissions)
- Optional: `com.google.acrt:acrt-permissions` for advanced flows
- Optional: Custom permission manager library

**Security Risks:**
- Permission bypass via UI confusion
- Confirmation dialog fatigue (user blindly accepts)
- Privilege escalation through confirmed actions
- Confirmation prompt spoofing

**Confirmation Required:**
- All permission requests (system policy)
- All destructive actions
- All cross-app data access
- All external API calls
- All skill installations
- All configuration changes

## Data Model Updates

### Enhanced PipelineContext
```kotlin
data class PipelineContext(
    val goal: String,
    val conversationHistory: List<ClaudeMessage>,
    val systemPrompt: String,
    val availableTools: Map<String, AndroidTool>,
    val plan: Plan? = null,
    val currentStepIndex: Int = 0,
    val stepResults: List<StepResult> = emptyList(),
    val checkpoints: List<Checkpoint> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
   ...Phase 2 additions...
    val browserSession: BrowserSession? = null,
    val ocrContext: OCRContext? = null,
    val agentId: String = UUID.randomUUID().toString(),
    val executionId: String = UUID.randomUUID().toString(),
    val pauseToken: CancellationToken? = null,
)
```

### Enhanced Checkpoint
```kotlin
data class Checkpoint(
    val id: String = UUID.randomUUID().toString(),
    val stage: PipelineStage,
    val context: PipelineContext,
    val timestamp: Long = System.currentTimeMillis(),
    val resumeId: String = UUID.randomUUID().toString(),  // For multi-agent resume
    val progress: Float = 0f,  // Progress percentage
)
```

### Memory Context RAG Extension
```kotlin
data class MemoryContext(
    val durableFacts: String,
    val recentNotes: String,
    val relevantMemories: List<SearchMatch> = emptyList(),
    val semanticMatches: List<SemanticMatch> = emptyList(),
    val vectorMemories: List<VectorMemoryEntry> = emptyList(),  // NEW
)
```

## Implementation Order & Dependencies

### Phase 2 Order (Critical Path)

1. **Advanced Confirmation & Permission System** (Foundation for all other features)
   - Depends on: Phase 1 architecture
   - Risks: Low
   - Tests: Permission request flow, confirmation dialog handling

2. **Memory RAG Enhancement** (Enables intelligent agent decisions)
   - Depends on: #1, Phase 1 MemoryInterfaces
   - Risks: Medium (embedding model)
   - Tests: Semantic search accuracy, vector similarity

3. **Skills System Refactoring** (Extensibility foundation)
   - Depends on: #1, Phase 1 tools/
   - Risks: Low-Medium (APK signing)
   - Tests: Skill install/verify, dependency resolution

4. **Browser Agent** (Primary use case for visual grounding)
   - Depends on: #1, WebBrowserTool, HttpTool
   - Risks: Medium (URL safety)
   - Tests: Browser navigation, DOM interaction, download

5. **OCR + Visual Grounding** (Enables screen understanding)
   - Depends on: #1, ScreenCaptureTool, Camera
   - Risks: Medium (image privacy)
   - Tests: OCR accuracy, UI element detection

6. **Multi-Agent Coordinator** (Scalability)
   - Depends on: #1-#5, AgentRuntime
   - Risks: Medium (coordination complexity)
   - Tests: Agent registration, task distribution, consensus

7. **Code Sandbox** (Capability expansion)
   - Depends on: #1, ExecutionPipeline
   - Risks: High (code execution safety)
   - Tests: Timeout enforcement, output capture, isolation

8. **Agent Console** (Operator UI)
   - Depends on: #1-#7, Compose UI
   - Risks: Low (display-only)
   - Tests: UI responsiveness, state display, command handling

9. **Pause/Resume/Stop Controls** (Execution control)
   - Depends on: #1, TaskState, ExecutionPipeline
   - Risks: Low-Medium (state corruption)
   - Tests: Pause/resume cycle, stop cleanup, persistence

10. **Long-Running Autonomous Execution** (Capstone)
    - Depends on: #1-#9
    - Risks: High (unbounded execution)
    - Tests: Timeout handling, checkpoint integrity, restart recovery

## Test/Verification Requirements

### Unit Tests
- [ ] MemoryStore operations (durable facts, daily notes, search)
- [ ] SemanticMemoryStore addDocument/semanticSearch
- [ ] Plan creation and dependency resolution
- [ ] PlanStep execution ordering
- [ ] Pipeline stage processing (each of 8 stages)
- [ ] Checkpoint save/load cycle
- [ ] TaskState enum properties (isTerminal, canResume, isActive)
- [ ] ConfirmationManager workflow
- [ ] Permission request flow

### Integration Tests
- [ ] ExecutionPipeline full run with mock tools
- [ ] Checkpoint save/load across pipeline stages
- [ ] Multi-agent coordinator registration
- [ ] Skills manager install/verify
- [ ] Browser agent navigation flow
- [ ] OCR grounding pipeline
- [ ] Code sandbox timeout enforcement

### Instrumented Tests (Device)
- [ ] Runtime permission handling
- [ ] Browser agent on Android WebView
- [ ] OCR with camera input
- [ ] Code sandbox on-device execution
- [ ] Agent console UI interaction
- [ ] Pause/resume from background
- [ ] Long task checkpoint recovery

### Safety Tests
- [ ] Code sandbox escape prevention
- [ ] Permission denial graceful handling
- [ ] Confirmation dialog correctness
- [ ] Skill signature verification
- [ ] Memory data isolation between users

## Android-Specific Considerations

### Manifest Updates (Phase 2)
- Add RECEIVE_BOOT_COMPLETED for auto-resume
- Add FOREGROUND_SERVICE for long-running tasks
- Add REQUEST_IGNORE_POWER_ENERGYoptimization for battery exemptions
- Add USE_FULL_SCREEN_INTENT for overlay windows
- Add ACCESSIBILITY_SERVICE for UI automation
- Add BIND_ACCESSIBILITY_SERVICE for accessibility

### Permissions (Phase 2)
**Normal:** INTERNET, ACCESS_NETWORK_STATE
**Dangerous (user confirmation required):**
- WRITE_EXTERNAL_STORAGE / MediaStore
- READ_PHONE_STATE
- CALL_PHONE
- SEND_SMS
- RECORD_AUDIO
- CAMERA
- ACCESSIBILITY_SERVICE
- SYSTEM_ALERT_WINDOW
- FOREGROUND_SERVICE

**Special:**
- MANAGE_EXTERNAL_STORAGE (Android 11+)
- REQUEST_IGNORE_POWER_ENERGYoptimization

### Gradle Configuration
- compileSdk: 36 (retain from Phase 1)
- targetSdk: 34 (retain from Phase 1)
- minSdk: 29 (retain from Phase 1)
- AGP: 8.9.1 (retain from Phase 1)
- Kotlin: 2.2.0 (retain from Phase 1)

### Dependencies Management
- Keep existing dependency versions
- Add new dependencies with version catalog entries
- No breaking changes to existing API surface

## Security Plan Summary

| Area | Risk Level | Mitigation |
|------|-----------|------------|
| Code Execution | Critical | Process isolation, timeout, output filtering, never auto-run |
| Permissions | High | Rationale dialogs, permission groups, remember/never, audit log |
| Browser/OCR | High | URL allowlist, image consent, opt-in models, data retention policy |
| Multi-Agent | Medium | Agent identity verification, task deduplication, resource quotas |
| Skills | High | Signature verification, permission scanning, marketplace review |
| Memory/RAG | Medium | Data isolation, embedding model sandboxing, privacy filtering |
| Console UI | Low | Input sanitization, log redaction, credential masking |
| Long-Running | Critical | Checkpoint intervals, timeout enforcement, forced stop recovery |

## Migration from Phase 1

**Backward Compatibility:**
- All Phase 1 APIs remain unchanged
- File-based memory persists
- Existing tools continue to work
- Checkpoint format compatible

**Migration Path:**
1. Phase 1 is frozen and tagged
2. Phase 2 adds new interfaces alongside existing ones
3. Optional migration of existing data to RACMemoryStore
4. New features opt-in via configuration
5. No breaking changes to Gradle or app namespace

## Building & Running

```bash
# Phase 1 (frozen)
./gradlew :app:assembleDebug

# Phase 2 development
./gradlew :app:assembleDebug -Pphase=2

# Run unit tests
./gradlew :app:test

# Run instrumentation
./gradlew :app:connectedAndroidTest
```