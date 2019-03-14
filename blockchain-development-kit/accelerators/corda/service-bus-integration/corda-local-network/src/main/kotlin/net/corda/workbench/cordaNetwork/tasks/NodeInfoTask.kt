package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File

/**
 * A Data task which makes calls to all nodes on the network to gather
 * their status and config information
 */
class NodeInfoTask(val ctx: TaskContext,private val nodeName: String) : DataTask<NodeInfoTask.NodeInfo> {

    override fun exec(executionContext: ExecutionContext): NodeInfo {
        val results = ArrayList<NodeInfo>()
            val nodeStatus = NodeStatusTask(ctx, nodeName).exec(executionContext)
            //nodeStatus["node"] = node.name

            val config = NodeConfigTask(ctx, nodeName).exec(executionContext)
            val result = NodeInfo(name = nodeName, legalName = config.legalName,
                    rpcPort = config.port, sshPort = config.sshPort,
                    socketTest = nodeStatus["socketTest"] as String,
                    sshConnectionTest = nodeStatus["sshConnectionTest"] as String)


        return result;
    }



    data class NodeInfo(val name: String,
                        val rpcPort: Int, val sshPort: Int, val legalName: String,
                        val socketTest: String, val sshConnectionTest: String)
}
