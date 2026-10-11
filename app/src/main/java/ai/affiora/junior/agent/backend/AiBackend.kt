package ai.affiora.junior.agent.backend

import ai.affiora.junior.agent.AiProvider
import ai.affiora.junior.data.model.ClaudeRequest
import ai.affiora.junior.data.model.ClaudeResponse

sealed interface AiBackend {
    suspend fun send(
        request: ClaudeRequest,
        apiKey: String,
        provider: AiProvider,
        onTextDelta: ((String) -> Unit)? = null,
        onThinkingStarted: (() -> Unit)? = null,
        /** Optional baseUrl override (used by CUSTOM provider for self-hosted endpoints). */
        baseUrlOverride: String? = null,
    ): ClaudeResponse
}
