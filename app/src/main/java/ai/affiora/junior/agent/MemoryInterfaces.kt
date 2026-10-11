package ai.affiora.junior.agent

import kotlinx.coroutines.flow.Flow

/**
 * Core interface for memory storage systems.
 * Implementations can be file-based, vector-based, or hybrid.
 */
interface MemoryStore {
    /** Append a durable fact that persists across sessions. */
    suspend fun appendDurableFact(fact: String, tags: List<String> = emptyList())
    
    /** Read all durable facts, truncated to maxChars. */
    suspend fun readDurableFacts(maxChars: Int = 8000): String
    
    /** Delete durable facts containing the substring. */
    suspend fun deleteDurableFactContaining(substring: String): Boolean
    
    /** List all durable facts. */
    suspend fun listDurableFacts(): List<String>
    
    /** Append a daily note. */
    suspend fun appendDailyNote(note: String)
    
    /** Read recent daily notes (today + yesterday by default). */
    suspend fun readRecentDailyNotes(days: Int = 2, maxChars: Int = 2000): String
    
    /** Search across all memory with a query. */
    suspend fun search(query: String): List<SearchMatch>
    
    /** Read the dreams/reflection diary. */
    suspend fun readDreamsDiary(): String
    
    /** Append a dreams entry. */
    suspend fun appendDreamsEntry(section: String, content: String)
}

/**
 * Extended interface for semantic/vector memory capabilities.
 * Future implementations can add vector search, RAG, etc.
 */
interface SemanticMemoryStore : MemoryStore {
    /** Add a document to the semantic index. */
    suspend fun addDocument(id: String, content: String, metadata: Map<String, String> = emptyMap())
    
    /** Search by semantic similarity (vector search). */
    suspend fun semanticSearch(query: String, limit: Int = 10): List<SemanticMatch>
    
    /** Get document by ID. */
    suspend fun getDocument(id: String): Document?
    
    /** Delete document from index. */
    suspend fun deleteDocument(id: String)
    
    /** Rebuild the vector index. */
    suspend fun rebuildIndex()
}

/**
 * Match from text-based search.
 */
data class SearchMatch(
    val source: String,
    val line: String,
    val score: Double,
)

/**
 * Match from semantic/vector search.
 */
data class SemanticMatch(
    val id: String,
    val content: String,
    val score: Double,
    val metadata: Map<String, String> = emptyMap(),
)

/**
 * Document in the semantic store.
 */
data class Document(
    val id: String,
    val content: String,
    val embedding: FloatArray? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

/**
 * Configuration for memory behavior.
 */
data class MemoryConfig(
    /** Maximum characters for durable facts in context. */
    val maxDurableChars: Int = 8000,
    
    /** Maximum characters for daily notes in context. */
    val maxDailyChars: Int = 2000,
    
    /** Whether to enable semantic search (requires SemanticMemoryStore). */
    val enableSemanticSearch: Boolean = false,
    
    /** Maximum results for semantic search. */
    val semanticSearchLimit: Int = 10,
    
    /** Similarity threshold for semantic search (0.0 - 1.0). */
    val similarityThreshold: Double = 0.7,
)

/**
 * Memory context for injection into prompts.
 */
data class MemoryContext(
    val durableFacts: String,
    val recentNotes: String,
    val relevantMemories: List<SearchMatch> = emptyList(),
    val semanticMatches: List<SemanticMatch> = emptyList(),
) {
    fun toPromptString(): String {
        return buildString {
            if (durableFacts.isNotBlank()) {
                appendLine("## Durable Facts")
                appendLine(durableFacts)
                appendLine()
            }
            if (recentNotes.isNotBlank()) {
                appendLine("## Recent Notes")
                appendLine(recentNotes)
                appendLine()
            }
            if (relevantMemories.isNotEmpty()) {
                appendLine("## Relevant Memories")
                for (match in relevantMemories) {
                    appendLine("- [${match.source}] ${match.line}")
                }
                appendLine()
            }
            if (semanticMatches.isNotEmpty()) {
                appendLine("## Semantically Relevant")
                for (match in semanticMatches) {
                    appendLine("- [${match.score}%] ${match.content.take(200)}")
                }
                appendLine()
            }
        }
    }
}

/**
 * Builder for memory context.
 */
class MemoryContextBuilder(
    private val memoryStore: MemoryStore,
    private val semanticStore: SemanticMemoryStore? = null,
    private val config: MemoryConfig = MemoryConfig(),
) {
    suspend fun build(query: String? = null): MemoryContext {
        val durable = memoryStore.readDurableFacts(config.maxDurableChars)
        val daily = memoryStore.readRecentDailyNotes(2, config.maxDailyChars)
        
        val relevant = if (query != null) {
            memoryStore.search(query)
        } else emptyList()
        
        val semantic = if (config.enableSemanticSearch && semanticStore != null && query != null) {
            semanticStore.semanticSearch(query, config.semanticSearchLimit)
                .filter { it.score >= config.similarityThreshold }
        } else emptyList()
        
        return MemoryContext(
            durableFacts = durable,
            recentNotes = daily,
            relevantMemories = relevant,
            semanticMatches = semantic,
        )
    }
}
