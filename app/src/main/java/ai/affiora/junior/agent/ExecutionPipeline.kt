package ai.affiora.junior.agent

import ai.affiora.junior.data.model.AgentEvent
import ai.affiora.junior.data.model.ClaudeMessage
import ai.affiora.junior.tools.AndroidTool
import ai.affiora.junior.tools.ToolResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.text.buildString
import kotlinx.serialization.json.JsonPrimitive
import java.util.UUID

/**
 * Represents the execution pipeline stages for the JUNIOR agent.
 * UNDERSTAND → PLAN → EXECUTE → OBSERVE → VERIFY → CHECKPOINT → CONTINUE → REPORT
 */
enum class PipelineStage {
    UNDERSTAND,  // Analyze the user's goal and context
    PLAN,        // Create an execution plan
    EXECUTE,     // Execute the plan steps
    OBSERVE,     // Observe the results of execution
    VERIFY,      // Verify results against expectations
    CHECKPOINT,  // Save checkpoint for resume capability
    CONTINUE,    // Continue to next step or iterate
    REPORT       // Report final results to user
}

/**
 * Context passed through the pipeline stages.
 */
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
)

/**
 * Checkpoint for task resume capability.
 */
data class Checkpoint(
    val id: String = UUID.randomUUID().toString(),
    val stage: PipelineStage,
    val context: PipelineContext,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Pipeline stage processor interface.
 */
interface StageProcessor {
    val stage: PipelineStage
    
    suspend fun process(context: PipelineContext): StageResult
}

/**
 * Result of a pipeline stage.
 */
sealed class StageResult {
    data class Continue(val nextContext: PipelineContext) : StageResult()
    data class WaitForInput(val nextContext: PipelineContext, val prompt: String) : StageResult()
    data class Complete(val finalContext: PipelineContext, val result: String) : StageResult()
    data class Error(val error: String) : StageResult()
}

/**
 * Main pipeline orchestrator.
 */
class ExecutionPipeline(
    private val stageProcessors: List<StageProcessor>,
) {
    private val processorsByStage = stageProcessors.associateBy { it.stage }
    
    suspend fun execute(initialContext: PipelineContext): Flow<AgentEvent> = flow {
        var currentContext = initialContext
        var currentStage = PipelineStage.UNDERSTAND
        
        while (true) {
            val processor = processorsByStage[currentStage]
                ?: throw IllegalStateException("No processor for stage: $currentStage")
            
            val result = processor.process(currentContext)
            
            when (result) {
                is StageResult.Continue -> {
                    currentContext = result.nextContext
                    currentStage = getNextStage(currentStage)
                }
                is StageResult.WaitForInput -> {
                    currentContext = result.nextContext
                    emit(AgentEvent.NeedsConfirmation(
                        toolName = "pipeline",
                        preview = result.prompt,
                        requestId = UUID.randomUUID().toString()
                    ))
                    // Pipeline pauses here, waiting for user input
                    currentStage = PipelineStage.CONTINUE
                }
                is StageResult.Complete -> {
                    currentContext = result.finalContext
                    emit(AgentEvent.Text(result.result))
                    break
                }
                is StageResult.Error -> {
                    emit(AgentEvent.Error(result.error))
                    break
                }
            }
            
            // Safety check to prevent infinite loops
            if (currentStage == PipelineStage.REPORT) break
        }
    }
    
    private fun getNextStage(current: PipelineStage): PipelineStage {
        return when (current) {
            PipelineStage.UNDERSTAND -> PipelineStage.PLAN
            PipelineStage.PLAN -> PipelineStage.EXECUTE
            PipelineStage.EXECUTE -> PipelineStage.OBSERVE
            PipelineStage.OBSERVE -> PipelineStage.VERIFY
            PipelineStage.VERIFY -> PipelineStage.CHECKPOINT
            PipelineStage.CHECKPOINT -> PipelineStage.CONTINUE
            PipelineStage.CONTINUE -> PipelineStage.EXECUTE // Loop back for next step
            PipelineStage.REPORT -> PipelineStage.REPORT
        }
    }
}

/**
 * Understanding stage - analyzes the goal and context.
 */
class UnderstandStage : StageProcessor {
    override val stage = PipelineStage.UNDERSTAND
    
    override suspend fun process(context: PipelineContext): StageResult {
        // Analyze the goal, extract intent, identify required capabilities
        val understoodContext = context.copy(
            metadata = context.metadata + ("understood" to "true")
        )
        return StageResult.Continue(understoodContext)
    }
}

/**
 * Planning stage - creates an execution plan.
 */
class PlanStage(
    private val planner: Planner,
) : StageProcessor {
    override val stage = PipelineStage.PLAN
    
    override suspend fun process(context: PipelineContext): StageResult {
        val plan = planner.createPlan(context.goal, context.availableTools.values.toList())
        val plannedContext = context.copy(
            plan = plan,
            currentStepIndex = 0,
            stepResults = emptyList(),
        )
        return StageResult.Continue(plannedContext)
    }
}

/**
 * Execution stage - executes the current plan step.
 */
class ExecuteStage : StageProcessor {
    override val stage = PipelineStage.EXECUTE
    
    override suspend fun process(context: PipelineContext): StageResult {
        val plan = context.plan ?: return StageResult.Error("No plan available")
        val stepIndex = context.currentStepIndex
        
        if (stepIndex >= plan.steps.size) {
            // All steps executed, move to verification
            return StageResult.Continue(context.copy(metadata = context.metadata + ("all_steps_done" to "true")))
        }
        
        val step = plan.steps[stepIndex]
        val tool = context.availableTools[step.toolName]
        
        if (tool == null) {
            return StageResult.Error("Tool not found: ${step.toolName}")
        }
        
        // Execute the tool
        val toolParams = step.parameters.mapValues { (_, v) -> JsonPrimitive(v) }
        val result = tool.execute(toolParams)
        
        val stepResult = when (result) {
            is ToolResult.Success -> StepResult.Success(step.id, result.data)
            is ToolResult.Error -> StepResult.Failure(step.id, result.message)
            is ToolResult.NeedsConfirmation -> StepResult.NeedsInput(step.id, result.preview, StepResult.InputType.CONFIRMATION)
        }
        
        val updatedResults = context.stepResults + stepResult
        val nextContext = context.copy(
            stepResults = updatedResults,
            currentStepIndex = stepIndex + 1,
        )
        
        if (result is ToolResult.NeedsConfirmation) {
            return StageResult.WaitForInput(nextContext, result.preview)
        }
        
        return StageResult.Continue(nextContext)
    }
}

/**
 * Observation stage - observes and records the results.
 */
class ObserveStage : StageProcessor {
    override val stage = PipelineStage.OBSERVE
    
    override suspend fun process(context: PipelineContext): StageResult {
        // Log observations, update metrics, etc.
        return StageResult.Continue(context)
    }
}

/**
 * Verification stage - verifies results against expectations.
 */
class VerifyStage : StageProcessor {
    override val stage = PipelineStage.VERIFY
    
    override suspend fun process(context: PipelineContext): StageResult {
        val plan = context.plan ?: return StageResult.Error("No plan available")
        val lastResult = context.stepResults.lastOrNull()
        
        // Check if verification criteria are met
        if (lastResult is StepResult.Failure) {
            // Could trigger retry or plan refinement
            return StageResult.Continue(context.copy(metadata = context.metadata + ("verification_failed" to "true")))
        }
        
        return StageResult.Continue(context.copy(metadata = context.metadata + ("verified" to "true")))
    }
}

/**
 * Checkpoint stage - saves checkpoint for resume capability.
 */
class CheckpointStage(
    private val checkpointStore: CheckpointStore,
) : StageProcessor {
    override val stage = PipelineStage.CHECKPOINT
    
    override suspend fun process(context: PipelineContext): StageResult {
        val checkpoint = Checkpoint(
            stage = PipelineStage.CHECKPOINT,
            context = context,
        )
        checkpointStore.save(checkpoint)
        return StageResult.Continue(context.copy(checkpoints = context.checkpoints + checkpoint))
    }
}

/**
 * Continue stage - determines whether to continue or complete.
 */
class ContinueStage : StageProcessor {
    override val stage = PipelineStage.CONTINUE
    
    override suspend fun process(context: PipelineContext): StageResult {
        val plan = context.plan ?: return StageResult.Error("No plan available")
        
        if (context.currentStepIndex >= plan.steps.size) {
            // All steps done, move to report
            return StageResult.Complete(
                context.copy(metadata = context.metadata + ("completed" to "true")),
                "Task completed successfully."
            )
        }
        
        // Continue to next step
        return StageResult.Continue(context)
    }
}

/**
 * Report stage - reports final results to user.
 */
class ReportStage : StageProcessor {
    override val stage = PipelineStage.REPORT
    
    override suspend fun process(context: PipelineContext): StageResult {
        val summary = buildSummary(context)
        return StageResult.Complete(context, summary)
    }
    
    private fun buildSummary(context: PipelineContext): String {
        val plan = context.plan ?: return "No plan executed."
        val completed = context.stepResults.count { it is StepResult.Success }
        val failed = context.stepResults.count { it is StepResult.Failure }
        
        return buildString {
            appendLine("## Task Summary")
            appendLine("**Goal:** ${context.goal}")
            appendLine("**Steps:** ${completed}/${plan.steps.size} completed")
            if (failed > 0) appendLine("**Failed:** $failed")
            appendLine()
            appendLine("### Results:")
            for ((index, result) in context.stepResults.withIndex()) {
                val step = plan.steps[index]
                when (result) {
                    is StepResult.Success -> appendLine("✅ ${step.description}: ${result.output.take(100)}")
                    is StepResult.Failure -> appendLine("❌ ${step.description}: ${result.error}")
                    is StepResult.NeedsInput -> appendLine("⏳ ${step.description}: Waiting for input")
                }
            }
        }
    }
}

/**
 * Store for checkpoints (persist to disk/database).
 */
interface CheckpointStore {
    suspend fun save(checkpoint: Checkpoint)
    suspend fun load(taskId: String): Checkpoint?
    suspend fun list(): List<Checkpoint>
    suspend fun delete(taskId: String)
}

/**
 * Default file-based checkpoint store.
 */
class FileCheckpointStore(
    private val checkpointDir: java.io.File,
) : CheckpointStore {
    
    init { checkpointDir.mkdirs() }
    
    override suspend fun save(checkpoint: Checkpoint) {
        // TODO: Implement JSON serialization and file writing
    }
    
    override suspend fun load(taskId: String): Checkpoint? {
        // TODO: Implement file reading and deserialization
        return null
    }
    
    override suspend fun list(): List<Checkpoint> {
        // TODO: Implement listing
        return emptyList()
    }
    
    override suspend fun delete(taskId: String) {
        // TODO: Implement deletion
    }
}
