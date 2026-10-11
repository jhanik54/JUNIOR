package ai.affiora.junior.agent

/**
 * Represents the lifecycle state of a task in the JUNIOR agent system.
 * 
 * State transitions:
 *   CREATED → PLANNED → RUNNING
 *   RUNNING → WAITING (awaiting user input/confirmation)
 *   RUNNING → PAUSED (user paused)
 *   WAITING → RUNNING (user responded)
 *   PAUSED → RESUMED (user resumed)
 *   RESUMED → RUNNING
 *   RUNNING → VERIFYING (checking results)
 *   VERIFYING → COMPLETED (success)
 *   VERIFYING → FAILED (verification failed)
 *   Any state → CANCELLED (user cancelled)
 */
enum class TaskState {
    /** Task has been created but not yet planned */
    CREATED,
    /** Task has been planned with executable steps */
    PLANNED,
    /** Task is actively executing */
    RUNNING,
    /** Task is waiting for user input/confirmation */
    WAITING,
    /** Task has been paused by user */
    PAUSED,
    /** Task has been resumed after pause */
    RESUMED,
    /** Task is verifying execution results */
    VERIFYING,
    /** Task completed successfully */
    COMPLETED,
    /** Task failed */
    FAILED,
    /** Task was cancelled by user */
    CANCELLED;

    /** Whether the task is in a terminal state */
    val isTerminal: Boolean
        get() = this in setOf(COMPLETED, FAILED, CANCELLED)

    /** Whether the task can be resumed */
    val canResume: Boolean
        get() = this in setOf(PAUSED, WAITING)

    /** Whether the task is actively processing */
    val isActive: Boolean
        get() = this in setOf(RUNNING, VERIFYING)
}
