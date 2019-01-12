package net.corda.workbench.commons.taskManager

import java.io.File
import java.util.UUID

/**
 * Generic definition of a Task, which we simply execute and which
 * may optionally return a value.
 */
interface Task {
    val taskID: UUID
    fun exec(executionContext: ExecutionContext = ExecutionContext())
}

interface DataTask<T> {
    fun taskID(): UUID = UUID.randomUUID()
    fun exec(executionContext: ExecutionContext = ExecutionContext()): T
}

abstract class BaseTask : Task {
    override val taskID: UUID = UUID.randomUUID()
}

/**
 * A task that runs over all the nodes
 * TODO - need to make the
 */
abstract class NodesTask(protected val ctx: TaskContext) : BaseTask() {
    fun nodesIter(): Sequence<File> {
        return File(ctx.workingDir).walk()
                .maxDepth(1)
                .filter { it.isDirectory && it.name.endsWith("_node") }
    }

}

/**
 * Simple data class to hold basic logging information
 */
data class TaskLogMessage(val message: String, val taskId: UUID, val timestamp: Long = System.currentTimeMillis())
