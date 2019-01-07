package net.corda.workbench.cordaNetwork.tasks

import com.typesafe.config.ConfigFactory
import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.nio.file.Paths

/**
 * A Data task which reads values from the node config
 */
class NodeConfigTask(val ctx: TaskContext,
                     private val nodeName: String) : DataTask<NodeConfigTask.Config> {

    override fun exec(executionContext: ExecutionContext): Config {
        synchronized("NodeConfigTask") {
            ConfigFactory.invalidateCaches()

            val nodeConfFile = Paths.get(ctx.workingDir, standardiseNodeName(nodeName), "node.conf").normalize().toFile()
            System.setProperty("config.file", nodeConfFile.absolutePath)
            val config = ConfigFactory.load()

            val port = config.getString("rpcSettings.address").split(":")[1].toInt()
            val sshPort = config.getInt("sshd.port")
            return Config(legalName = config.getString("myLegalName"), port = port, sshPort = sshPort)
        }
    }

    fun standardiseNodeName(name: String): String {
        return if (name.endsWith("_node")) name else name.toLowerCase() + "_node"
    }

    data class Config(val legalName: String, val port: Int, val sshPort: Int)

}