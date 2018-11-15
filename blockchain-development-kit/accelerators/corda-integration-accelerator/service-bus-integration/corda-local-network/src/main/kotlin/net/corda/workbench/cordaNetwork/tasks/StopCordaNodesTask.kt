package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext

/**
 * Stops each of the nodes
 */
class StopCordaNodesTask(ctx: TaskContext) : NodesTask(ctx) {

    override fun exec(executionContext: ExecutionContext) {

        for (node in nodesIter()) {
            StopCordaNodeTask(ctx, node.name).exec(executionContext)
        }
    }
}
