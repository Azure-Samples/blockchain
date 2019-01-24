package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import sun.security.x509.X500Name
import java.io.File

/**
 * A Data task which reads the list of node names
 */
class NodeListTask(val ctx: TaskContext) : DataTask<List<String>> {

    override fun exec(executionContext: ExecutionContext): List<String> {
        val results = ArrayList<String>()
        for (node in nodesIter()) {
            val config = NodeConfigTask(ctx, node.name).exec()

            val x500 = X500Name(config.legalName)
            results.add(x500.organization)

        }
        if (results.isEmpty()) throw RuntimeException("no network found at ${ctx.workingDir}")
        return results
    }

    fun nodesIter(): Sequence<File> {
        return File(ctx.workingDir).walk()
                .maxDepth(1)
                .filter { it.isDirectory && it.name.endsWith("_node") }
    }
}