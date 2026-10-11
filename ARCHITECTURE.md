# JUNIOR Architecture Documentation

## Overview

JUNIOR is an AI-powered Android agent that can control your device, automate tasks, and integrate with your favorite services. This document describes the architecture after Phase 1 refactoring from MobileClaw.

## Package Structure

```
ai.affiora.junior
├── agent/              # Core agent runtime
│   ├── AgentRuntime.kt       # Main agent execution loop
│   ├── AgentService.kt       # Foreground service for background operation
│   ├── AiProvider.kt         # AI provider definitions
│   ├── MemoryStore.kt        # File-based memory implementation
│   ├── MemoryInterfaces.kt   # Clean interfaces for memory (future vector/RAG)
│   ├── Planner.kt            # Planning abstraction
│   ├── ExecutionPipeline.kt  # Structured pipeline stages
│   ├── TaskState.kt          # Task lifecycle states
│   ├── SystemPromptBuilder.kt# System prompt construction
│   ├── ClaudeApiClient.kt    # AI API client with failover
│   ├── LocalInferenceEngine.kt # On-device inference
│   ├── LocalModelManager.kt  # Local model management
│   ├── LocalToolAdapter.kt   # Tool adapter for local inference
│   ├── PermissionManager.kt  # Tool permission management
│   ├── RetryPolicy.kt        # Cross-provider failover policy
│   ├── ScheduleEngine.kt     # Recurring task scheduling
│   ├── ContextCompactor.kt   # Conversation compaction
│   └── backend/              # AI provider backends
├── channels/           # Messaging channels (Telegram, Slack, etc.)
├── connectors/         # OAuth connectors
├── data/               # Data layer
│   ├── db/                 # Room database
│   ├── model/              # Data models
│   └── prefs/              # User preferences (DataStore)
├── di/                 # Dependency injection (Hilt)
├── gateway/            # Firebase messaging (Phase 3)
├── plugins/            # Plugin system
├── skills/             # Skills management
├── tools/              # Android tools (30+ tools)
├── ui/                 # Compose UI
│   ├── chat/               # Chat screen
│   ├── cron/               # Scheduling screen
│   ├── devices/            # Device pairing (Phase 3)
│   ├── onboarding/         # First-run experience
│   ├── settings/           # Settings screens
│   ├── skills/             # Skills library
│   └── theme/              # Material3 theme
└── util/               # Utilities
```

## Core Architecture Components

### 1. AgentRuntime

The main agent execution loop that:
- Manages conversation history
- Calls AI APIs with tool definitions
- Executes tool calls and handles confirmations
- Emits events for UI consumption

Key methods:
- `run(userMessage, history, systemPrompt)` - Start new agent turn
- `runWithHistory(history, systemPrompt)` - Continue with existing history
- `confirmAction(requestId, confirmed)` - Handle user confirmations

### 2. Execution Pipeline (New in Phase 1)

Structured pipeline with 8 stages:
```
UNDERSTAND → PLAN → EXECUTE → OBSERVE → VERIFY → CHECKPOINT → CONTINUE → REPORT
```

Each stage is a `StageProcessor` that can be customized:
- **UNDERSTAND**: Analyze goal and context
- **PLAN**: Create executable plan using Planner
- **EXECUTE**: Run plan steps via tools
- **OBSERVE**: Record observations
- **VERIFY**: Check results against expectations
- **CHECKPOINT**: Save state for resume
- **CONTINUE**: Decide next action
- **REPORT**: Generate final summary

### 3. Planning Abstraction (New in Phase 1)

- **Plan**: Goal + ordered steps with dependencies
- **PlanStep**: Single tool invocation with parameters
- **Planner interface**: `createPlan(goal, tools)` + `refinePlan()`
- **LLMPlanner**: Default implementation using AI

### 4. Task State Management (New in Phase 1)

```kotlin
enum class TaskState {
    CREATED, PLANNED, RUNNING, WAITING, PAUSED, 
    RESUMED, VERIFYING, COMPLETED, FAILED, CANCELLED
}
```

Helper properties:
- `isTerminal` - COMPLETED, FAILED, CANCELLED
- `canResume` - PAUSED, WAITING
- `isActive` - RUNNING, VERIFYING

### 5. Memory Architecture (Enhanced in Phase 1)

**MemoryStore interface** - Core contract:
- Durable facts (MEMORY.md)
- Daily notes (daily/YYYY-MM-DD.md)
- Dreams diary (DREAMS.md)
- Text search

**SemanticMemoryStore interface** - Future vector/RAG:
- `addDocument()`, `semanticSearch()`
- `getDocument()`, `deleteDocument()`
- `rebuildIndex()`

**MemoryContextBuilder** - Unified context for prompts:
- Combines durable facts, daily notes, search results
- Optional semantic matches
- Clean prompt string generation

### 6. Checkpoint/Resume Support (New in Phase 1)

- **Checkpoint**: Serializable state at pipeline stage
- **CheckpointStore interface**: Save/load/list/delete
- **FileCheckpointStore**: File-based implementation
- Integrates with ExecutionPipeline at CHECKPOINT stage

### 7. AI Providers

14 providers supported:
- Anthropic (API Key + Setup Token)
- OpenAI / OpenAI Codex
- Google Gemini
- OpenRouter
- Mistral, Together, Groq, xAI, DeepSeek, Fireworks
- Qwen, StepFun, NVIDIA NIM, z.ai
- Custom (Ollama/LM Studio/vLLM)
- Amazon Bedrock
- Local (Gemma 4 on-device)

Features:
- Automatic failover with RetryPolicy
- Per-provider model selection
- Streaming with real-time text deltas
- Thinking/interleaved support for Anthropic

### 8. Tools (30+)

Categories:
- **System**: Alarm, Brightness, Volume, Flashlight, Clipboard
- **Communication**: SMS, Call Log, Contacts, Phone Call, Telegram
- **Productivity**: Calendar, File System, Browser, HTTP
- **Media**: Photo, Screen Capture, Media Control
- **Automation**: UI Automation, App Launcher, Navigation
- **AI**: Memory, Session History, Skill Author, Sub-Agent
- **Device**: System Info, Notification Listener, Accessibility

### 9. Skills System

- Built-in skills (15+)
- User-installed skills from URL or ClawHub
- Security scanning before install
- AI-assisted skill adaptation
- Triggers for automatic activation

### 10. Channels

Messaging integrations:
- Telegram, Slack, Matrix, Discord (via Matrix)
- WhatsApp, Signal, SMS
- Feishu, Teams, Email
- Push notifications

## Data Flow

```
User Input → ChatViewModel → AgentRuntime → AI API
                    ↓              ↓
              Database         Tool Registry
              (Room)           (30+ tools)
                    ↓              ↓
              Events ←──────── Tool Results
                    ↓
              UI (Compose)
```

## Key Design Principles

1. **Incremental Refactoring** - No big-bang rewrites, preserve working code
2. **Clean Interfaces** - Memory, Planning, Pipeline have interfaces for testing/swapping
3. **Backward Compatibility** - Existing file-based memory, skills, tools all work
4. **Separation of Concerns** - Agent, Tools, Memory, UI are decoupled
5. **Extensibility** - New providers, tools, channels, skills via interfaces
6. **Security** - Permission system, confirmation dialogs, skill scanning

## Future Enhancements (Phase 2+)

- Multi-agent swarm coordination
- Advanced browser automation (Playwright)
- OCR/Visual grounding
- Code sandbox execution
- Plugin marketplace
- Vector memory with RAG
- Cross-device synchronization

## Building

```bash
./gradlew :app:assembleDebug
```

## Testing

```bash
./gradlew :app:test
./gradlew :app:connectedAndroidTest
```
