# JUNIOR — Android AI Agent

**JUNIOR** is an AI-powered Android agent that automates device tasks using natural language commands. Tell your phone what to do, and it handles the rest — tapping, typing, reading, calling, navigating, and controlling apps via Android's accessibility APIs.

*   **No server. No subscription. Your phone does everything.**
*   **On-device AI:** Run Gemma 4 entirely offline via LiteRT-LM — no API key, no internet, no cost.
*   **Multi-provider cloud:** Bring your own keys for Anthropic, OpenAI, Google Gemini, OpenRouter, and 7+ more providers.
*   **Extensible skills:** 21 built-in skills (Morning Routine, Email, Messaging, Navigation, Smart Home, Finance, Health, etc.) plus user-created skills via Markdown + YAML frontmatter.
*   **Privacy first:** All data stays on your device. Encrypted API key storage. No analytics, no tracking, no telemetry.

---

## Quick Start

```bash
git clone https://github.com/jhanik54/JUNIOR.git
cd JUNIOR
./gradlew assembleDebug
```

Install the APK on your Android device. Then:

*   **Cloud:** Enter an API key (Anthropic, OpenAI, Google, etc.) in Settings > AI Provider.
*   **On-Device:** Go to Settings > AI Provider > On-Device and download Gemma 4.

Grant requested permissions (SMS, calls, contacts, accessibility, etc.), enable the Accessibility Service, and start chatting.

---

## On-Device AI

Run **Gemma 4** directly on your phone via [LiteRT-LM](https://github.com/google-ai-edge/LiteRT-LM). No API key. No internet. No cost.

*   **E2B** (2.6 GB) — 6 GB+ RAM, ~9 tok/s on GPU
*   **E4B** (3.7 GB) — 8 GB+ RAM, higher quality responses
*   **GPU accelerated** with NPU/CPU fallback (auto-detected)
*   **Tool calling** works on-device via constrained decoding
*   Download models from Settings, switch between cloud and local anytime

---

## Tools

30+ native tools that directly access Android APIs:

| SMS | Call Log | Contacts | Phone Call |
|-----|----------|----------|------------|
| Calendar | Alarms | Notifications | App Launcher |
| Navigation | UI Automation | Screen Capture | Web Browser |
| HTTP API | File System | Photos | Clipboard |
| Media Control | Volume | Brightness | Flashlight |
| System Info | Scheduled Tasks | Skill Author | Memory |
| Session History | Sub-Agent | Channel | Telegram |

Every tool works with both cloud and on-device models.

---

## Skills

21 composable skills (Markdown + YAML frontmatter):

**Built-in:** morning-routine, email, messaging-apps, navigation, notification-digest, phone-basics, photos, self-learning, social-media, telegram-bot, translation, ui-fallback, weather, web-research

**Vertical:** finance, health, notion, real-estate, shopping, smart-home, telegram

Create your own: describe what you want, and the AI writes the skill for you (`/create` command).

---

## Architecture

```
┌─────────────────────────────────────────────┐
│              Jetpack Compose UI             │
│   ChatScreen · Skills · Settings            │
├─────────────────────┬─────────────────────┤
│      AgentRuntime    │   LiteRT-LM Engine    │
│   Tool-use loop    │   (Gemma 4 on-device) │
├─────────────────────┴─────────────────────┤
│ ClaudeApiClient · 10+ Cloud Providers     │
├─────────────────────────────────────────────┤
│ Room · DataStore · EncryptedPrefs         │
├─────────────────────────────────────────────┤
│ AccessibilityService · Notifications        │
└─────────────────────────────────────────────┘
```

**Stack:** Kotlin · Jetpack Compose · Hilt · Room · LiteRT-LM 0.10 · Anthropic SDK · Ktor

---

## Features (Implemented vs Planned)

| Feature | Status | Details |
|---------|--------|---------|
| **Natural language chat** | ✅ Implemented | AI agent loop with tool use and streaming |
| **On-device Gemma 4 AI** | ✅ Implemented | Via LiteRT-LM, no API key needed |
| **30+ Android tools** | ✅ Implemented | SMS, calls, contacts, calendar, web, files, photos, UI automation, etc. |
| **21 built-in skills** | ✅ Implemented | Morning routine, email, messaging, navigation, skills verticals, etc. |
| **Multi-provider cloud AI** | ✅ Implemented | Anthropic, OpenAI, Google Gemini, OpenRouter, Mistral, Together, Groq, xAI, DeepSeek, Fireworks |
| **Skill creation (/create)** | ✅ Implemented | AI generates Markdown+YAML skill files |
| **Voice input / TTS** | ⚠️ Partial | Speech-to-text and text-to-speech infrastructure available; full voice control workflow described in source |
| **Background automation** | ⚠️ Partial | Foreground service with permissions; exact alarm scheduling for timers/alarms. Some background limitations apply per Android API restrictions. |
| **Memory / Session history** | ✅ Implemented | Durable facts, daily notes, dreams diary, conversation history |
| **Channels (Telegram/SMS)** | ✅ Implemented | Messaging integrations with bot tokens and SMS sending |
| **Browser automation** | ❌ Not implemented | Planned for Phase 2; not yet available |
| **OCR / Visual grounding** | ❌ Not implemented | Planned for Phase 2; not yet available |
| **Multi-agent coordination** | ❌ Not implemented | Planned for Phase 2; not yet available |

---

## Quick Start

```bash
git clone https://github.com/jhanik54/JUNIOR.git
cd JUNIOR
./gradlew assembleDebug
```

Install the APK, then configure:

*   **Cloud:** Enter an API key (Anthropic, OpenAI, Google, etc.) in Settings > AI Provider.
*   **On-Device:** Go to Settings > AI Provider > On-Device > Download Gemma 4.

Grant permissions as needed, enable Accessibility Service, and start chatting.

---

## Architecture

```
┌─────────────────────────────────────────────┐
│              Jetpack Compose UI             │
│   ChatScreen · Skills · Settings            │
├─────────────────────┬─────────────────────┤
│      AgentRuntime    │   LiteRT-LM Engine    │
│   Tool-use loop    │   (Gemma 4 on-device) │
├─────────────────────┴─────────────────────┤
│ ClaudeApiClient · 10+ Cloud Providers     │
├─────────────────────────────────────────────┤
│ Room · DataStore · EncryptedPrefs         │
├─────────────────────────────────────────────┤
│ AccessibilityService · Notifications        │
└─────────────────────────────────────────────┘
```

**Stack:** Kotlin 2.2 · Jetpack Compose · Hilt · Room · LiteRT-LM 0.10 · Anthropic SDK · Ktor

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). PRs welcome.

---

## License

MIT — see [LICENSE](LICENSE).

Built by the JUNIOR contributors. Originally based on [OpenClaw](https://github.com/openclaw/openclaw) by Peter Steinberger and contributors.