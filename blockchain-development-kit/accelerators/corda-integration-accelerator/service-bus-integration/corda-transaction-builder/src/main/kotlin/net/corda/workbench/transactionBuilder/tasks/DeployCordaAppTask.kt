package net.corda.workbench.transactionBuilder.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.io.File

class DeployCordaAppTask(registry: Registry, private val cordapp: File) : BaseTask() {

    val ctx = registry.retrieve(TaskContext::class.java)
    val es = registry.retrieve(EventStore::class.java)

    override fun exec(executionContext: ExecutionContext) {
        executionContext.messageStream.invoke("Deploying cordapp ${cordapp.name}")
        val target = "${ctx.workingDir}/cordapps/${cordapp.name}"
        cordapp.copyTo(File(target), true)
        es.storeEvents(listOf(EventFactory.CORDA_APP_DEPLOYED(cordapp.name, ctx.networkName)))
    }
}