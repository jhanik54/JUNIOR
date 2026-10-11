# JUNIOR Phase 2 Implementation Plan

## Implementation Order

Features are ordered by critical path dependency. Phase 1 foundation must be stable before any Phase 2 work begins.

### Phase 2.0: Foundation & Safety (Weeks 1-2)
**Goal**: Establish confirmation/permission system and safety infrastructure

**Tasks:**
- [ ] Implement AdvancedConfirmationManager with granular confirmation workflow
- [ ] Enhance PermissionManager with rationale dialogs and remember/never logic
- [ ] Add audit logging for all confirmed actions
- [ ] Implement Permission request flow with ShouldShowRequestPermissionRationale
- [ ] Update AgentRuntime to use new confirmation system
- [ ] Add security audit logger

**Deliverables:**
- ConfirmationManager.kt
- Enhanced PermissionManager.kt
- AuditLogger.kt
- AgentRuntime confirmation flow updates

**Exit Criteria:**
- All permission requests go through confirmation manager
- Audit log records every confirmed/destroyed action
- Graceful handling of permission denials

---

### Phase 2.1: Memory RAG Enhancement (Weeks 2-3)
**Goal**: Add vector-based semantic memory with on-device embeddings

**Tasks:**
- [ ] Integrate TensorFlow Lite embedding model (small footprint)
- [ ] Implement VectorMemory interface with addDocument/semanticSearch
- [ ] Build vector index using on-device embeddings
- [ ] Extend MemoryContext with vectorMemories field
- [ ] Update MemoryContextBuilder to include semantic matches
- [ ] Implement memory pruning with forgetting curve
- [ ] Add cross-session context retention

**Deliverables:**
- VectorMemory.kt interface + LocalVectorMemory implementation
- Embedding model wrapper (TFLite)
- Vector pruning utility
- MemoryContext vector extension

**Exit Criteria:**
- Vector index builds successfully on device
- Semantic search returns relevant results
- Memory pruning runs without data loss
- Cross-session memories persist

---

### Phase 2.2: Skills System Refactoring (Weeks 3-4)
**Goal**: Advanced skills management with versioning and marketplace

**Tasks:**
- [ ] Implement SkillsManager central registry
- [ ] Add skill dependency resolution
- [ ] Implement skill versioning (semantic versioning)
- [ ] Add skill signature verification (APK signing)
- [ ] Implement user-installed skills from URL
- [ ] Add skills marketplace API integration points
- [ ] AI-assisted skill adaptation framework

**Deliverables:**
- SkillsManager.kt with registry
- Skill metadata model (id, version, permissions, signature)
- Dependency resolver
- Installer with signature verification
- Adaptation engine scaffold

**Exit Criteria:**
- Skills install with verified signatures
- Dependency conflicts resolved or reported
- User-installed skills from URL work
- Adaptation scaffold ready for AI integration

---

### Phase 2.3: Browser Agent (Weeks 4-5)
**Goal**: Advanced browser automation with visual grounding

**Tasks:**
- [ ] Implement BrowserAgent.kt with step-based automation
- [ ] Extend WebBrowserTool with DOM interaction methods
- [ ] Add browser session management with checkpointing
- [ ] Implement visual grounding integration point
- [ ] Add URL allowlist and safety filtering
- [ ] Implement download management with user confirmation
- [ ] Add JavaScript execution with permissions

**Deliverables:**
- BrowserAgent.kt orchestrating browser steps
- WebBrowserTool extensions (DOM, elements, grounding)
- BrowserSession with checkpoint save/load
- URL allowlist/filter system

**Exit Criteria:**
- Browser agent navigates to allowed URLs
- DOM element interaction works
- Downloads prompt for confirmation
- Visual grounding integration point exists

---

### Phase 2.4: OCR + Visual/UI Grounding (Weeks 5-6)
**Goal**: Screen OCR and UI element detection/grounding

**Tasks:**
- [ ] Implement OCRGrounding.kt with camera/screen OCR
- [ ] Integrate play-services-vision OCR API
- [ ] Add accessibility service UI element tree
- [ ] Implement visual element localization
- [ ] Add first-time OCR enabling confirmation
- [ ] Implement accessibility service activation flow
- [ ] Add image capture permission handling

**Deliverables:**
- OCRGrounding.kt with screen/camera OCR
- VisionOCR wrapper (play-services-vision)
- UIElementDetector with localization
- OCR enabling confirmation dialog

**Exit Criteria:**
- OCR works on screen capture input
- UI elements detected and localized
- First-use OCR enabling flows work
- Accessibility service activation flow works

---

### Phase 2.5: Multi-Agent Coordinator (Weeks 6-7)
**Goal**: Agent swarm/team orchestration

**Tasks:**
- [ ] Implement MultiAgentCoordinator with agent registry
- [ ] Define WorkerAgent role/identity model
- [ ] Add task decomposition across agents
- [ ] Implement consensus protocol scaffold
- [ ] Add agent state synchronization (Room/Datastore)
- [ ] Implement cross-agent task distribution
- [ ] Add notification for agent status changes

**Deliverables:**
- MultiAgentCoordinator.kt
- WorkerAgent abstract class / interface
- AgentRegistry with register/discover
- Task decomposition utilities
- State sync scaffold (Room or DataStore)

**Exit Criteria:**
- Agents can register and discover each other
- Tasks can be decomposed across agents
- State sync works across app restarts
- Notification for agent status changes

---

### Phase 2.6: Code Sandbox (Weeks 7-8)
**Goal**: Secure code execution with isolation

**Tasks:**
- [ ] Implement CodeSandbox.kt with ProcessBuilder isolation
- [ ] Add timeout enforcement (wall clock + CPU time)
- [ ] Implement output capture and filtering
- [ ] Add resource limits (memory, file descriptors)
- [ ] Add sandboxed filesystem (chroot / tmpfs scaffold)
- [ ] Implement network isolation from sandbox
- [ ] Add escape prevention measures

**Deliverables:**
- CodeSandbox.kt execute() with timeout
- SandboxResult with output/exitCode
- Resource limiter utilities
- Escape prevention scaffold

**Exit Criteria:**
- Code executes within timeout
- Output captured and filtered
- Resource limits enforced
- No system resource leakage

---

### Phase 2.7: Agent Console (Weeks 8-9)
**Goal**: Operator UI console for agent monitoring and control

**Tasks:**
- [ ] Implement AgentConsole Compose UI
- [ ] Add real-time agent state display
- [ ] Live tool execution log
- [ ] Performance metrics dashboard
- [ ] Command palette for quick actions
- [ ] Searchable filter for logs
- [ ] Pull-to-refresh implementation

**Deliverables:**
- Console Compose UI (screen, themes, interactions)
- AgentState display component
- ExecutionLog component
- MetricsDashboard component
- CommandPalette component

**Exit Criteria:**
- UI displays agent state correctly
- Logs are searchable and filterable
- Command palette handles quick actions
- Performance metrics show live data

---

### Phase 2.8: Pause/Resume/Stop Controls (Weeks 9-10)
**Goal**: Unified pause/resume/stop with persistence

**Tasks:**
- [ ] Implement PauseResumeController with pause/resume/stop
- [ ] Add stop confirmation dialog (destructive action)
- [ ] Implement pause persistence across app restarts
- [ ] Add hotkey/gesture shortcuts
- [ ] Implement foreground service lifecycle management
- [ ] Add notification for pause state
- [ ] Add back button handling for pause

**Deliverables:**
- PauseResumeController.kt
- StopConfirmationDialog (Compose AlertDialog)
- Pause persistence (Room + DataStore)
- Hotkey/gesture handling scaffold

**Exit Criteria:**
- Pause/resume cycle works across app restarts
- Stop confirmation dialog appears for destructive stops
- Foreground service manages lifecycle correctly
- Notification shows pause state

---

### Phase 2.9: Long-Running Autonomous Execution (Weeks 10-12)
**Goal**: Autonomous task execution with checkpointing

**Tasks:**
- [ ] Implement LongRunningTaskManager with autonomous execution
- [ ] Add step timeout handling with cancellation
- [ ] Implement progress reporting (percent, ETA)
- [ ] Add automatic checkpoint at configurable intervals
- [ ] Implement break/resume from interrupted state
- [ ] Integrate WorkManager for constrained tasks
- [ ] Add battery optimization exemption handling

**Deliverables:**
- LongRunningTaskManager.kt with autonomous execution
- Progress reporter with ETA
- Checkpoint scheduler with intervals
- WorkManager integration scaffold
- Battery exemption request flow

**Exit Criteria:**
- Autonomous tasks execute with checkpoints
- Progress reports with ETA work
- Checkpoints save state at intervals
- Break/resume from interrupted state works

---

### Phase 2.10: Security & Permissions Hardening (Weeks 12-13)
**Goal**: Comprehensive security and permission system

**Tasks:**
- [ ] Harden AdvancedConfirmationManager
- [ ] Add permission audit logging
- [ ] Implement permission groups management
- [ ] Add security audit report generation
- [ ] Enhance CodeSandbox escape prevention
- [ ] Add skills signature verification complete
- [ ] Implement browser URL allowlist enforcement
- [ ] Add OCR/visual grounding data retention policy

**Deliverables:**
- Security audit report generation
- Permission group registry
- Complete audit trail
- Enhanced escape prevention

**Exit Criteria:**
- All actions audited
- Permission groups operational
- Security report generates without errors
- All high-risk areas mitigated

---

## Dependency Chain

```
Phase 2.0 (Confirmation/Permission)
    ↓
Phase 2.1 (Memory RAG)          ← uses enhanced confirmation
    ↓
Phase 2.2 (Skills System)       ← uses confirmation for installs
    ↓
Phase 2.3 (Browser Agent)       ← uses confirmation for URLs/downloads
    ↓
Phase 2.4 (OCR Grounding)       ← uses confirmation for accessibility
    ↓
Phase 2.5 (Multi-Agent)         ← uses confirmation for delegation
    ↓
Phase 2.6 (Code Sandbox)        ← uses confirmation for execution
    ↓
Phase 2.7 (Agent Console)       ← displays audit logs
    ↓
Phase 2.8 (Pause/Resume/Stop)   ← uses confirmation for stop
    ↓
Phase 2.9 (Long-Running)        ← uses checkpoints from checkpoint system
    ↓
Phase 2.10 (Security Hardening) ← wraps all above
```

## Android-Specific Implementation Notes

### Gradle / Version Catalog Updates

Add to `gradle/libs.versions.toml`:
```toml
# Phase 2 new dependencies
tflite = "2.16.0"
play-services-vision = "21.1.0"
camera-view = "1.1.0"
work-runtime = "2.9.0"
lifecycle-viewmodel = "2.7.0"
compose-bom = "2024.01.00"

# Keep existing
androidx-work = "2.9.0"
hilt-version = "2.50"
kotlin = "2.2.0"
agp = "8.9.1"
```

### Manifest Updates (Phase 2)

Add to `AndroidManifest.xml` (after Phase 1 config):

```xml
<!-- Phase 2: Long-running tasks -->
<service
    android:name=".agent.AgentService"
    android:foregroundServiceType="dataTransfer"
    android:exported="false">
    <intent-filter>
        <action android:name="ai.affiora.junior.ACTION_LONG_RUNNING"/>
    </intent-filter>
</service>

<!-- Phase 2: Battery optimization exemption -->
<meta-data
    android:name="androidx.work.broadcast.enabled"
    android:value="true"/>

<!-- Phase 2: Accessibility service (if not already) -->
<service
    android:name=".tools.ClawAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService"/>
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_config"/>
</service>

<!-- Phase 2: Foreground service permission -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_TRANSFER"/>
```

### Permissions (Phase 2 - add to Manifest)

**Dangerous permissions to add:**
```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission CALL_PHONE"/>
<uses-permission android:name="android.permission.SEND_SMS"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.ACCESSIBILITY_SERVICE"/>
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.REQUEST_IGNORE_POWER_ENERGYoptimization"/>
```

**Special:**
```xml
<!-- Android 11+ external storage management -->
<permission android:name="ai.affiora.junior.permission.MANAGE_EXTERNAL_STORAGE"
    android:protectionLevel="signature"/>
```

### Dependency Risk Assessment

| Dependency | Risk | Mitigation |
|-----------|------|------------|
| play-services-vision | Medium | Optional; fall back to Tesseract native |
| tensorflow-lite | Low | On-device, no external download needed |
| WorkManager | Low | AndroidX stable, well-tested |
| CameraView | Low | AndroidX, simple API |
| play-services | Medium | Google services dependency, optional |
| Custom embedding model | Low | Bundled with app, small footprint |

## Verification Requirements

### Unit Test Checklist
- [ ] ConfirmationManager: all confirmation types handled
- [ ] PermissionManager: request flow with rationale
- [ ] VectorMemory: addDocument/semanticSearch accuracy
- [ ] SkillsManager: install/verify/dependency resolution
- [ ] BrowserAgent: navigation, element interaction, download
- [ ] OCRGrounding: screen/camera OCR, UI element detection
- [ ] MultiAgentCoordinator: registration, task distribution
- [ ] CodeSandbox: timeout, output, resource limits
- [ ] PauseResumeController: pause/resume/stop cycle
- [ ] LongRunningTaskManager: checkpoints, progress, timeout
- [ ] AdvancedConfirmationManager: audit log completeness

### Integration Test Checklist
- [ ] Full ExecutionPipeline run with mock tools
- [ ] Checkpoint save/load across all pipeline stages
- [ ] Multi-agent registration and task distribution
- [ ] Skills install from URL with verification
- [ ] Browser agent on Android WebView
- [ ] OCR with camera input on device
- [ ] Code sandbox on-device execution (timeout)
- [ ] Agent console UI interaction
- [ ] Pause/resume from background mode
- [ ] Long task checkpoint recovery after interruption

### Safety Test Checklist
- [ ] Code sandbox escape prevention (never escapes process)
- [ ] Permission denial graceful handling (features degrade gracefully)
- [ ] Confirmation dialog correctness (no unexpected accepts)
- [ ] Skill signature verification (all installs verified)
- [ ] Browser URL allowlist enforcement (blocked URLs rejected)
- [ ] Memory data isolation between users (no cross-contamination)
- [ ] Long task forced stop recovery (state restored from checkpoint)

## Rollback Plan

**If Phase 2 introduces critical issues:**

1. **Gradle flag**: `./gradlew :app:assembleDebug -Pphase=1` reverts to Phase 1
2. **Manifest**: Phase 2 permissions can be removed from AndroidManifest.xml
3. **Dependencies**: Remove Phase 2 dependency entries from libs.versions.toml
4. **Data**: Phase 1 file-based memory persists unchanged
5. **Git**: `git reset --hard HEAD~1` rolls back last Phase 2 commit

**Rollback triggers:**
- Build fails on `./gradlew :app:assembleDebug`
- Device becomes unresponsive (force stop + clear data)
- Critical security vulnerability discovered
- User-reported data loss or corruption

## Success Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Permission success rate | >95% | Confirmed vs total requests |
| OCR accuracy (text > 3 chars) | >85% | Tesseract/play-services-vision |
| Browser navigation success | >90% | Allowed URLs vs total attempts |
| Code sandbox timeout | <2s enforced | Process kill at timeout |
| Multi-agent coordination | 100% registration | Agent registry completeness |
| Checkpoint recovery | 100% state restore | Post-restart resume |
| Audit log completeness | 100% actions logged | Every confirmed action |
| Confirmation dialog accuracy | >98% | Correct vs total dialogs |

## Notes

- Phase 1 is FROZEN - no changes to existing APIs
- All new features are OPT-IN via configuration
- Backward compatibility maintained at all times
- Security is DESIGN FIRST - every feature has threat modeling
- Native Android solutions preferred over external dependencies
- All features tested on arm64-v8a (Termux) and x86_64 (GitHub Actions)