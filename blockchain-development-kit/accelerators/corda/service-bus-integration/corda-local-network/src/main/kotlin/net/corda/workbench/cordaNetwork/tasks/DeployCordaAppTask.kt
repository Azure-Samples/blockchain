package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import net.corda.workbench.transactionBuilder.md5Hash
import java.io.File
import java.nio.file.Paths

/**
 * Deploys the supplies app to all nodes, overwriting any existing
 * apps.
 */
class DeployCordaAppTask(registry: Registry, private val cordapp: File) : NodesTask(registry.retrieve(TaskContext::class.java)) {
    private val es = registry.retrieve(EventStore::class.java)

    override fun exec(executionContext: ExecutionContext) {

        val masterCopy = Paths.get(ctx.workingDir, ".cordapps", cordapp.name).normalize()
                .toAbsolutePath()
                .toFile()
        masterCopy.mkdirs()
        cordapp.copyTo(masterCopy,true)

        for (node in nodesIter()) {
            executionContext.messageSink.invoke("Deploying cordapp ${cordapp.name} to ${node.name}")
            val target = Paths.get(node.toString(), "cordapps", cordapp.name)
                    .normalize()
                    .toAbsolutePath()
                    .toFile()
            cordapp.copyTo(target, true)
        }

        es.storeEvent(EventFactory.CORDAPP_DEPLOYED(ctx.networkName,
                cordapp.name,
                cordapp.length().toInt(),
                cordapp.md5Hash()))
    }
}




