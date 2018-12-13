package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.isSocketAlive

/**
 * A Data task which makes call to the node to determine its status
 */
class NodeStatusTask(val ctx: TaskContext,
                     private val nodeName: String) : DataTask<Map<String, Any>> {

    override fun exec(executionContext: ExecutionContext): Map<String, Any> {
        val config = NodeConfigTask(ctx, nodeName).exec()

        val socketTest = isSocketAlive("corda-local-network", config.port)
        val sshTest = isSocketAlive("corda-local-network", config.sshPort)

        val results = HashMap<String, Any>()
        results["socket test"] = resultOf(socketTest)
        results["ssh connection test"] = resultOf(sshTest)

        return results;
    }

    fun resultOf(b: Boolean): String {
        return if (b) "Passed" else "Failed"
    }
}