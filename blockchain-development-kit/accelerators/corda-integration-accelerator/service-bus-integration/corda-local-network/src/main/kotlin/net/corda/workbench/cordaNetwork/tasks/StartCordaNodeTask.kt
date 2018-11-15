package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.ProcessManager
import java.io.File

class StartCordaNodeTask(val ctx: TaskContext, private val nodeName: String) : BaseTask() {

    override fun exec(executionContext: ExecutionContext) {

        val nodeDir = ctx.workingDir + "/" + nodeName

        executionContext.messageStream.invoke("starting node $nodeName in $nodeDir")

        val pb = ProcessBuilder(listOf("java", "-jar", "corda.jar"))
                .directory(File(nodeDir))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()


        ProcessManager.register(ctx.networkName, nodeName, pb)
    }

}