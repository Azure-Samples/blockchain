package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.lang.Exception

/**
 * Stops each of the nodes
 */
class StopCordaNodesTask(val registry: Registry) : NodesTask(registry.retrieve(TaskContext::class.java)) {
    val es = registry.retrieve(EventStore::class.java)
    override fun exec(executionContext: ExecutionContext) {

        for (node in nodesIter()) {
            try {
                StopCordaNodeTask(registry, node.name).exec(executionContext)
            } catch (ex: Exception) {
                executionContext.messageStream("problem in StopCordaNodeTask for $node - ${ex.message}")
            }
        }
        es.storeEvent(EventFactory.NETWORK_STOPPED(ctx.networkName))
    }
}
