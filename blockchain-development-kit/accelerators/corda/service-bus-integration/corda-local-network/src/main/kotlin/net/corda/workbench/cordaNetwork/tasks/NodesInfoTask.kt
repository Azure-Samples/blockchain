package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File

/**
 * A Data task which makes calls to all nodes on the network to gather
 * their status and config information.
 */
class NodesInfoTask(val ctx: TaskContext) : DataTask<List<NodeInfoTask.NodeInfo>> {

    override fun exec(executionContext: ExecutionContext): List<NodeInfoTask.NodeInfo> {
        val results = ArrayList<NodeInfoTask.NodeInfo>()
        for (node in nodesIter()) {
            val info = NodeInfoTask(ctx, node.name.removeSuffix("_node")).exec(executionContext)
            results.add(info)
        }

        return results;
    }

    private fun nodesIter(): Sequence<File> {
        return File(ctx.workingDir).walk()
                .maxDepth(1)
                .filter { it.isDirectory && it.name.endsWith("_node") }
    }
}
