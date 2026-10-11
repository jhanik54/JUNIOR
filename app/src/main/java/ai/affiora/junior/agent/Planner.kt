package ai.affiora.junior.agent

import ai.affiora.junior.tools.AndroidTool
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A single executable step in a plan.
 */
@Serializable
data class PlanStep(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val toolName: String,
    val parameters: Map<String, String> = emptyMap(),
    val dependsOn: List<String> = emptyList(),
    val expectedResult: String? = null,
    val verificationCriteria: String? = null,
)

/**
 * A plan consisting of multiple steps to achieve a goal.
 */
@Serializable
data class Plan(
    val id: String = UUID.randomUUID().toString(),
    val goal: String,
    val steps: List<PlanStep> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
) {
    val stepCount: Int get() = steps.size
    
    /** Get steps in execution order (respecting dependencies) */
    fun getExecutionOrder(): List<PlanStep> {
        val result = mutableListOf<PlanStep>()
        val visited = mutableSetOf<String>()
        val visiting = mutableSetOf<String>()
        
        fun visit(step: PlanStep) {
            if (step.id in visited) return
            if (step.id in visiting) {
                throw IllegalStateException("Circular dependency detected in plan: ${step.id}")
            }
            visiting.add(step.id)
            for (depId in step.dependsOn) {
                val dep = steps.find { it.id == depId } ?: throw IllegalStateException("Missing dependency: $depId")
                visit(dep)
            }
            visiting.remove(step.id)
            visited.add(step.id)
            result.add(step)
        }
        
        for (step in steps) {
            visit(step)
        }
        return result
    }
}

/**
 * Result of executing a plan step.
 */
sealed class StepResult {
    data class Success(val stepId: String, val output: String, val artifacts: Map<String, String> = emptyMap()) : StepResult()
    data class Failure(val stepId: String, val error: String) : StepResult()
    data class NeedsInput(val stepId: String, val prompt: String, val inputType: InputType) : StepResult()
    
    enum class InputType {
        CONFIRMATION,
        TEXT_INPUT,
        SELECTION
    }
}

/**
 * Interface for planning strategies.
 * Different implementations can use different approaches (LLM-based, rule-based, etc.)
 */
interface Planner {
    /**
     * Create a plan to achieve the given goal using available tools.
     */
    suspend fun createPlan(goal: String, availableTools: List<AndroidTool>): Plan
    
    /**
     * Refine an existing plan based on execution results so far.
     */
    suspend fun refinePlan(plan: Plan, completedSteps: List<PlanStep>, results: List<StepResult>): Plan
}

/**
 * Default LLM-based planner implementation.
 * Uses the AI to break down goals into executable steps.
 */
class LLMPlanner(
    private val plannerAgent: PlanningAgent,
) : Planner {
    
    override suspend fun createPlan(goal: String, availableTools: List<AndroidTool>): Plan {
        return plannerAgent.generatePlan(goal, availableTools)
    }
    
    override suspend fun refinePlan(plan: Plan, completedSteps: List<PlanStep>, results: List<StepResult>): Plan {
        return plannerAgent.refinePlan(plan, completedSteps, results)
    }
}

/**
 * Agent interface for planning operations.
 * This can be implemented by different AI providers.
 */
interface PlanningAgent {
    suspend fun generatePlan(goal: String, availableTools: List<AndroidTool>): Plan
    suspend fun refinePlan(plan: Plan, completedSteps: List<PlanStep>, results: List<StepResult>): Plan
}
