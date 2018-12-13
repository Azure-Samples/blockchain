package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory

/**
 * Combine all the actions for creating nodes for a network
 */

class CreateNodesTask(private val registry: Registry, private val parties: List<String>) : BaseTask() {
    private val ctx = registry.retrieve(TaskContext::class.java)!!
    private val es = registry.retrieve(EventStore::class.java)!!

    override fun exec(executionContext: ExecutionContext) {

        ConfigBuilderTask(registry, parties).exec(executionContext)
        NetworkBootstrapperTask(ctx).exec(executionContext)

        es.storeEvent(EventFactory.NODES_CREATED(ctx.networkName, parties))

    }

}