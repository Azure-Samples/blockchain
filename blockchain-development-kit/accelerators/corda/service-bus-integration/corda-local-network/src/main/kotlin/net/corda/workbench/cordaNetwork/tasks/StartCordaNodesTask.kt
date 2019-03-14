package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory

/**
 * Starts each of the nodes
 */
class StartCordaNodesTask(private val registry: Registry) : NodesTask(registry.retrieve(TaskContext::class.java)) {
    val es = registry.retrieve(EventStore::class.java)
    override fun exec(executionContext: ExecutionContext) {
        for (node in nodesIter()) {
            StartCordaNodeTask(registry, node.name).exec(executionContext)
        }
        es.storeEvent(EventFactory.NETWORK_STARTED(ctx.networkName))
    }
}
