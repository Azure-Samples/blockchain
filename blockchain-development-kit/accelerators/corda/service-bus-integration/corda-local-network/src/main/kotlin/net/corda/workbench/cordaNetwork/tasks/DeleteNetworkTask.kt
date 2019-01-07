package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File

/**
 * Deletes a network.
 */
class DeleteNetworkTask(val ctx: TaskContext) : BaseTask() {

    override fun exec(executionContext: ExecutionContext) {

        // todo - should also be stopping tasks
        File(ctx.workingDir).deleteRecursively()
    }

}