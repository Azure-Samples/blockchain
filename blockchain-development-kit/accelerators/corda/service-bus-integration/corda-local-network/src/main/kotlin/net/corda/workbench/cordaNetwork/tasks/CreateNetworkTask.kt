package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.events.Repo
import net.corda.workbench.transactionBuilder.events.EventFactory

/**
 * Create a network (with default settings) if none exists. Very simple,
 * at the moment, just store an event in the eventstore
 */

class CreateNetworkTask(registry: Registry) : BaseTask() {
    private val ctx = registry.retrieve(TaskContext::class.java)!!
    private val es = registry.retrieve(EventStore::class.java)!!

    override fun exec(executionContext: ExecutionContext) {
        val repo = Repo(es)
        if (!repo.networks().map { it.name }.contains(ctx.networkName)) {
            es.storeEvent(EventFactory.NETWORK_CREATED(ctx.networkName))
        } else {
            throw RuntimeException("network ${ctx.networkName} already exists")
        }
    }
}