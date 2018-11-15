package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext

/**
 * Starts each of the nodes
 */
class StartCordaNodesTask(ctx: TaskContext) : NodesTask(ctx) {

    override fun exec(executionContext: ExecutionContext) {

        for (node in nodesIter()) {
            StartCordaNodeTask(ctx, node.name).exec(executionContext)
        }
    }
}
