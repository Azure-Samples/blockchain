package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.NodesTask
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File

/**
 * Deploys the supplies app to all nodes, overwriting any existing
 * apps.
 */
class DeployCordaAppTask(ctx: TaskContext, private val cordapp: File) : NodesTask(ctx) {

    override fun exec(executionContext: ExecutionContext) {
        for (f in nodesIter()) {
            executionContext.messageStream.invoke("Deploying cordapp ${cordapp.name} to ${f.name}")
            val target = "$f/cordapps/${cordapp.name}"
            cordapp.copyTo(File(target), true)
        }
    }
}




