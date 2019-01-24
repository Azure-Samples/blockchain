package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.events.CordaAppInfo
import net.corda.workbench.transactionBuilder.md5Hash
import java.nio.file.Paths

/**
 * A Data task which reads the Corda Apps on a nodes
 */
class NodeCordaAppsTask(val ctx: TaskContext,
                        nodeName: String) : DataTask<List<CordaAppInfo>> {

    val standardiseNodeName = standardiseNodeName(nodeName)

    override fun exec(executionContext: ExecutionContext): List<CordaAppInfo> {
        val results = ArrayList<CordaAppInfo>()

        val filePath = Paths.get(ctx.workingDir, standardiseNodeName, "cordapps").normalize()
        val directory = filePath.toFile()
        directory.list().forEach {
            val appFilePath = Paths.get(ctx.workingDir, standardiseNodeName, "cordapps", it).normalize()
            val cordaApp = appFilePath.toFile()
            val info = CordaAppInfo(name = cordaApp.name, size = cordaApp.length().toInt(),
                    md5Hash = cordaApp.md5Hash())
            results.add(info)
        }

        return results
    }

    private fun standardiseNodeName(name: String): String {
        return if (name.endsWith("_node")) name else name.toLowerCase() + "_node"
    }


}