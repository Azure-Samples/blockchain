package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext

import java.nio.file.Paths

/**
 * A Data task that returns full log for a node
 */
class NodeLogsTask(val ctx: TaskContext, nodeName: String) : DataTask<String> {

    private val standardiseNodeName = standardiseNodeName(nodeName)

    override fun exec(executionContext: ExecutionContext): String {

        val directory = Paths.get(ctx.workingDir, standardiseNodeName, "logs").normalize().toFile()

        // this pattern works locally
        directory.list().forEach {
            if (it.endsWith(".local.log")) {
                println ("Using $it logfile")
                val logFilePath = Paths.get(ctx.workingDir, standardiseNodeName, "logs", it).normalize()
                val log = logFilePath.toFile()
                return log.readText()
            }
        }

        // this pattern works under docker
        directory.list().forEach {
            if (it != "node-info-gen.log") {
                println ("Using $it logfile")
                val logFilePath = Paths.get(ctx.workingDir, standardiseNodeName, "logs", it).normalize()
                val log = logFilePath.toFile()
                return log.readText()
            }
        }

        return "";
    }

    private fun standardiseNodeName(name: String): String {
        return if (name.endsWith("_node")) name else name.toLowerCase() + "_node"
    }

}
